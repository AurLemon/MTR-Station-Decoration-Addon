package top.mcmtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import top.mcmtr.block.RigidCatenaryNodeBlock;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.operation.MSDUpdateDataRequest;
import top.mcmtr.init.MSDNeoForgeRuntime;
import top.mcmtr.item.RigidCatenaryConnectorItem;

public final class MSDProbeRigidCommand {

	private static final SimpleCommandExceptionType PROBE_FAILED = new SimpleCommandExceptionType(Component.literal("MSD rigid probe command failed"));
	private static final SimpleCommandExceptionType INVALID_NODE = new SimpleCommandExceptionType(Component.literal("MSD rigid probe requires rigid catenary nodes"));
	private static final SimpleCommandExceptionType INVALID_CONNECTED_VALUE = new SimpleCommandExceptionType(Component.literal("Expected connected value to be true/false or connected/disconnected"));

	private MSDProbeRigidCommand() {
	}

	public static void register(RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		final var root = Commands.literal("msd_probe_rigid")
				.requires(source -> source.hasPermission(2));

		final var connectCommand = Commands.literal("connect");
		final var connectFrom = Commands.argument("from", BlockPosArgument.blockPos());
		final var connectTo = Commands.argument("to", BlockPosArgument.blockPos())
				.executes(context -> runConnect(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "from"),
						BlockPosArgument.getLoadedBlockPos(context, "to")
				));
		connectFrom.then(connectTo);
		connectCommand.then(connectFrom);
		root.then(connectCommand);

		final var removeCommand = Commands.literal("remove");
		final var removeFrom = Commands.argument("from", BlockPosArgument.blockPos());
		final var removeTo = Commands.argument("to", BlockPosArgument.blockPos())
				.executes(context -> runRemove(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "from"),
						BlockPosArgument.getLoadedBlockPos(context, "to")
				));
		removeFrom.then(removeTo);
		removeCommand.then(removeFrom);
		root.then(removeCommand);

		root.then(Commands.literal("save").executes(context -> runSave(context.getSource())));

		final var shapeCommand = Commands.literal("shape");
		final var shapePos = Commands.argument("pos", BlockPosArgument.blockPos());
		final var shapeName = Commands.argument("shape", StringArgumentType.word());
		final var shapeRadius = Commands.argument("radius", DoubleArgumentType.doubleArg(0))
				.executes(context -> runShape(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "pos"),
						StringArgumentType.getString(context, "shape"),
						DoubleArgumentType.getDouble(context, "radius")
				));
		shapeName.then(shapeRadius);
		shapePos.then(shapeName);
		shapeCommand.then(shapePos);
		root.then(shapeCommand);

		final var assertCommand = Commands.literal("assert");
		final var assertPos = Commands.argument("pos", BlockPosArgument.blockPos());
		final var assertConnected = Commands.argument("connected", StringArgumentType.word())
				.executes(context -> runAssert(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "pos"),
						StringArgumentType.getString(context, "connected")
				));
		assertPos.then(assertConnected);
		assertCommand.then(assertPos);
		root.then(assertCommand);

		dispatcher.register(root);
	}

	private static int runConnect(CommandSourceStack source, BlockPos firstPos, BlockPos secondPos) throws CommandSyntaxException {
		final ServerLevel level = source.getLevel();
		final BlockState firstState = getNodeState(level, firstPos);
		final BlockState secondState = getNodeState(level, secondPos);
		final InteractionResult result = RigidCatenaryConnectorItem.connect(level, firstPos, secondPos, firstState, secondState);
		if (result.consumesAction()) {
			source.sendSuccess(() -> Component.literal("MSD rigid catenary connected"), true);
			return 1;
		}
		throw PROBE_FAILED.create();
	}

	private static int runRemove(CommandSourceStack source, BlockPos firstPos, BlockPos secondPos) throws CommandSyntaxException {
		final ServerLevel level = source.getLevel();
		final BlockState firstState = getNodeState(level, firstPos);
		final BlockState secondState = getNodeState(level, secondPos);
		final InteractionResult result = RigidCatenaryConnectorItem.remove(level, firstPos, secondPos, firstState, secondState);
		if (result.consumesAction()) {
			source.sendSuccess(() -> Component.literal("MSD rigid catenary removed"), true);
			return 1;
		}
		throw PROBE_FAILED.create();
	}

	private static int runSave(CommandSourceStack source) throws CommandSyntaxException {
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null || !runtime.saveNow()) {
			throw PROBE_FAILED.create();
		}
		source.sendSuccess(() -> Component.literal("MSD runtime saved"), true);
		return 1;
	}

	private static int runAssert(CommandSourceStack source, BlockPos pos, String connectedValue) throws CommandSyntaxException {
		final boolean expectedConnected = parseConnected(connectedValue);
		final BlockState state = getNodeState(source.getLevel(), pos);
		final boolean actualConnected = state.getValue(RigidCatenaryNodeBlock.IS_CONNECTED);
		if (actualConnected != expectedConnected) {
			throw PROBE_FAILED.create();
		}
		source.sendSuccess(() -> Component.literal("MSD rigid node assertion passed"), true);
		return 1;
	}

	private static int runShape(CommandSourceStack source, BlockPos pos, String shapeValue, double radius) throws CommandSyntaxException {
		final ServerLevel level = source.getLevel();
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null) {
			throw PROBE_FAILED.create();
		}

		final RigidCatenary rigidCatenary = runtime.getRigidCatenary(level, pos);
		if (rigidCatenary == null) {
			throw PROBE_FAILED.create();
		}

		final RigidCatenary.Shape shape = parseShape(shapeValue);
		final double clampedRadius = org.mtr.core.tool.Utilities.clampSafe(
				org.mtr.core.tool.Utilities.round(radius, 2),
				0,
				rigidCatenary.rigidCatenaryMath.getMaxVerticalRadius()
		);
		final RigidCatenary updatedRigidCatenary = RigidCatenary.copy(rigidCatenary, shape, clampedRadius);
		if (!runtime.sendUpdate(level, new MSDUpdateDataRequest(runtime.newClientData()).addRigidCatenary(updatedRigidCatenary))) {
			throw PROBE_FAILED.create();
		}

		source.sendSuccess(() -> Component.literal("MSD rigid catenary shape updated"), true);
		return 1;
	}

	private static boolean parseConnected(String value) throws CommandSyntaxException {
		if ("true".equalsIgnoreCase(value) || "connected".equalsIgnoreCase(value)) {
			return true;
		}
		if ("false".equalsIgnoreCase(value) || "disconnected".equalsIgnoreCase(value)) {
			return false;
		}
		throw INVALID_CONNECTED_VALUE.create();
	}

	private static RigidCatenary.Shape parseShape(String value) throws CommandSyntaxException {
		if ("quadratic".equalsIgnoreCase(value)) {
			return RigidCatenary.Shape.QUADRATIC;
		}
		if ("two_radii".equalsIgnoreCase(value) || "two-radii".equalsIgnoreCase(value)) {
			return RigidCatenary.Shape.TWO_RADII;
		}
		throw PROBE_FAILED.create();
	}

	private static BlockState getNodeState(ServerLevel level, BlockPos pos) throws CommandSyntaxException {
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof RigidCatenaryNodeBlock)) {
			throw INVALID_NODE.create();
		}
		return state;
	}
}
