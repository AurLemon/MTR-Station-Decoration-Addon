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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import top.mcmtr.block.AbstractStandingSignBlock;

public final class MSDProbeCustomTextCommand {

	private static final SimpleCommandExceptionType INVALID_CUSTOM_TEXT = new SimpleCommandExceptionType(Component.literal("MSD probe requires a standing sign block entity"));

	private MSDProbeCustomTextCommand() {
	}

	public static void register(RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		dispatcher.register(Commands.literal("msd_probe_custom_text")
				.requires(source -> source.hasPermission(2))
				.then(Commands.argument("pos", BlockPosArgument.blockPos())
						.then(Commands.argument("messages", StringArgumentType.greedyString())
								.executes(context -> runSet(
										context.getSource(),
										BlockPosArgument.getLoadedBlockPos(context, "pos"),
										StringArgumentType.getString(context, "messages")
								)))));
	}

	private static int runSet(CommandSourceStack source, BlockPos pos, String messagesArgument) throws CommandSyntaxException {
		final BlockEntity blockEntity = source.getLevel().getBlockEntity(pos);
		if (!(blockEntity instanceof AbstractStandingSignBlock.StandingSignBlockEntity standingSignBlockEntity)) {
			throw INVALID_CUSTOM_TEXT.create();
		}

		standingSignBlockEntity.setMessages(parseMessages(messagesArgument));
		source.sendSuccess(() -> Component.literal("MSD custom text updated"), true);
		return 1;
	}

	private static String[] parseMessages(String messagesArgument) {
		if (messagesArgument == null || messagesArgument.isEmpty()) {
			return new String[0];
		}
		return messagesArgument.split(";;", -1);
	}
}
