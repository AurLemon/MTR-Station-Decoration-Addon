package top.mcmtr.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import top.mcmtr.block.BlockCatenaryWithModel;
import top.mcmtr.block.YamanoteRailwaySignBlock;

public final class MSDProbeBlockEntityCommand {

	private static final SimpleCommandExceptionType INVALID_YAMANOTE_SIGN = new SimpleCommandExceptionType(Component.literal("MSD probe requires a yamanote sign block entity"));
	private static final SimpleCommandExceptionType INVALID_CATENARY_MODEL = new SimpleCommandExceptionType(Component.literal("MSD probe requires a catenary-with-model block entity"));

	private MSDProbeBlockEntityCommand() {
	}

	public static void register(RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		final var root = Commands.literal("msd_probe_block_entity")
				.requires(source -> source.hasPermission(2));

		root.then(Commands.literal("yamanote")
				.then(Commands.argument("pos", BlockPosArgument.blockPos())
						.then(Commands.argument("selected_ids", StringArgumentType.string())
								.then(Commands.argument("sign_ids", StringArgumentType.greedyString())
										.executes(context -> runYamanoteSet(
												context.getSource(),
												BlockPosArgument.getLoadedBlockPos(context, "pos"),
												StringArgumentType.getString(context, "selected_ids"),
												StringArgumentType.getString(context, "sign_ids")
										))))));

		root.then(Commands.literal("model")
				.then(Commands.argument("pos", BlockPosArgument.blockPos())
						.then(Commands.argument("offset_x", DoubleArgumentType.doubleArg(-1, 1))
								.then(Commands.argument("offset_y", DoubleArgumentType.doubleArg(-1, 1))
										.then(Commands.argument("offset_z", DoubleArgumentType.doubleArg(-1, 1))
												.then(Commands.argument("rotation_y", DoubleArgumentType.doubleArg(0, 360))
														.executes(context -> runModelSet(
																context.getSource(),
																BlockPosArgument.getLoadedBlockPos(context, "pos"),
																DoubleArgumentType.getDouble(context, "offset_x"),
																DoubleArgumentType.getDouble(context, "offset_y"),
																DoubleArgumentType.getDouble(context, "offset_z"),
																DoubleArgumentType.getDouble(context, "rotation_y")
														))))))));

		dispatcher.register(root);
	}

	private static int runYamanoteSet(CommandSourceStack source, BlockPos pos, String selectedIdsCsv, String signIdsCsv) throws CommandSyntaxException {
		final BlockEntity blockEntity = source.getLevel().getBlockEntity(pos);
		if (!(blockEntity instanceof YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity signBlockEntity)) {
			throw INVALID_YAMANOTE_SIGN.create();
		}

		final LongAVLTreeSet selectedIds = parseSelectedIds(selectedIdsCsv);
		final String[] signIds = parseSignIds(signIdsCsv, signBlockEntity.getSignIds().length);
		final LongAVLTreeSet[] selectedIdsBySlot = new LongAVLTreeSet[signIds.length];
		for (int i = 0; i < selectedIdsBySlot.length; i++) {
			selectedIdsBySlot[i] = new LongAVLTreeSet(selectedIds);
		}
		signBlockEntity.setData(selectedIdsBySlot, signIds);
		source.sendSuccess(() -> Component.literal("MSD yamanote sign updated"), true);
		return 1;
	}

	private static int runModelSet(CommandSourceStack source, BlockPos pos, double offsetX, double offsetY, double offsetZ, double rotationY) throws CommandSyntaxException {
		final BlockEntity blockEntity = source.getLevel().getBlockEntity(pos);
		if (!(blockEntity instanceof BlockCatenaryWithModel.BlockCatenaryWithModelEntity catenaryWithModelEntity)) {
			throw INVALID_CATENARY_MODEL.create();
		}

		catenaryWithModelEntity.setTransform(offsetX, offsetY, offsetZ, 0, rotationY, 0);
		source.sendSuccess(() -> Component.literal("MSD catenary-with-model updated"), true);
		return 1;
	}

	private static LongAVLTreeSet parseSelectedIds(String value) {
		final LongAVLTreeSet result = new LongAVLTreeSet();
		if (value == null || value.isBlank() || value.equals("-")) {
			return result;
		}

		Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(token -> !token.isEmpty())
				.forEach(token -> {
					try {
						result.add(Long.parseLong(token));
					} catch (NumberFormatException ignored) {
					}
				});
		return result;
	}

	private static String[] parseSignIds(String value, int length) {
		final String[] result = new String[length];
		if (value == null || value.isBlank()) {
			return result;
		}

		final String[] parts = value.split(",", -1);
		for (int i = 0; i < length && i < parts.length; i++) {
			final String normalized = parts[i].trim().toLowerCase(Locale.ENGLISH);
			result[i] = normalized.isEmpty() || normalized.equals("-") ? null : normalized;
		}
		return result;
	}
}
