package top.mcmtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.core.data.Catenary;
import top.mcmtr.core.data.CatenaryType;
import top.mcmtr.init.MSDNeoForgeRuntime;
import top.mcmtr.item.CatenaryConnectorItem;

public final class MSDProbeCatenaryCommand {

	private static final SimpleCommandExceptionType PROBE_FAILED = new SimpleCommandExceptionType(Component.literal("MSD catenary probe command failed"));
	private static final SimpleCommandExceptionType INVALID_NODE = new SimpleCommandExceptionType(Component.literal("MSD catenary probe requires old catenary nodes"));
	private static final SimpleCommandExceptionType INVALID_CONNECTED_VALUE = new SimpleCommandExceptionType(Component.literal("Expected connected value to be true/false or connected/disconnected"));
	private static final SimpleCommandExceptionType INVALID_TYPE = new SimpleCommandExceptionType(Component.literal("Expected catenary type to be catenary/electric/rigid_soft"));

	private MSDProbeCatenaryCommand() {
	}

	public static void register(RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		final var root = Commands.literal("msd_probe_catenary")
				.requires(source -> source.hasPermission(2));

		final var connectCommand = Commands.literal("connect");
		final var connectFrom = Commands.argument("from", BlockPosArgument.blockPos());
		final var connectTo = Commands.argument("to", BlockPosArgument.blockPos());
		connectTo.then(Commands.literal("catenary")
				.executes(context -> runConnect(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "from"),
						BlockPosArgument.getLoadedBlockPos(context, "to"),
						CatenaryType.CATENARY
				)));
		connectTo.then(Commands.literal("electric")
				.executes(context -> runConnect(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "from"),
						BlockPosArgument.getLoadedBlockPos(context, "to"),
						CatenaryType.ELECTRIC
				)));
		connectTo.then(Commands.literal("rigid_soft")
				.executes(context -> runConnect(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "from"),
						BlockPosArgument.getLoadedBlockPos(context, "to"),
						CatenaryType.RIGID_SOFT_CATENARY
				)));
		connectTo.then(Commands.literal("rigid-soft")
				.executes(context -> runConnect(
						context.getSource(),
						BlockPosArgument.getLoadedBlockPos(context, "from"),
						BlockPosArgument.getLoadedBlockPos(context, "to"),
						CatenaryType.RIGID_SOFT_CATENARY
				)));
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

	private static int runConnect(CommandSourceStack source, BlockPos firstPos, BlockPos secondPos, CatenaryType catenaryType) throws CommandSyntaxException {
		final ServerLevel level = source.getLevel();
		final BlockState firstState = getNodeState(level, firstPos);
		final BlockState secondState = getNodeState(level, secondPos);
		final InteractionResult result = CatenaryConnectorItem.connect(level, firstPos, secondPos, firstState, secondState, catenaryType);
		if (result.consumesAction()) {
			source.sendSuccess(() -> Component.literal("MSD catenary connected"), true);
			return 1;
		}
		throw PROBE_FAILED.create();
	}

	private static int runRemove(CommandSourceStack source, BlockPos firstPos, BlockPos secondPos) throws CommandSyntaxException {
		final ServerLevel level = source.getLevel();
		final BlockState firstState = getNodeState(level, firstPos);
		final BlockState secondState = getNodeState(level, secondPos);
		final InteractionResult result = CatenaryConnectorItem.remove(level, firstPos, secondPos, firstState, secondState);
		if (result.consumesAction()) {
			source.sendSuccess(() -> Component.literal("MSD catenary removed"), true);
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
		final boolean actualConnected = state.getValue(OldNodeBlock.IS_CONNECTED);
		if (actualConnected != expectedConnected) {
			throw PROBE_FAILED.create();
		}

		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null) {
			throw PROBE_FAILED.create();
		}

		final Catenary catenary = runtime.getCatenary(source.getLevel(), pos);
		if ((catenary != null) != expectedConnected) {
			throw PROBE_FAILED.create();
		}

		source.sendSuccess(() -> Component.literal("MSD catenary node assertion passed"), true);
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

	private static BlockState getNodeState(ServerLevel level, BlockPos pos) throws CommandSyntaxException {
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof OldNodeBlock)) {
			throw INVALID_NODE.create();
		}
		return state;
	}
}
