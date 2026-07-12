package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import top.mcmtr.block.AbstractStandingSignBlock;

public final class MSDPacketUpdateCustomText extends PacketHandler {

	private final BlockPos blockPos;
	private final String[] messages;

	public MSDPacketUpdateCustomText(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
		messages = new String[packetBufferReceiver.readInt()];
		for (int i = 0; i < messages.length; i++) {
			messages[i] = packetBufferReceiver.readString();
		}
	}

	public MSDPacketUpdateCustomText(BlockPos blockPos, String[] messages) {
		this.blockPos = blockPos;
		this.messages = messages.clone();
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeInt(messages.length);
		for (final String message : messages) {
			packetBufferSender.writeString(message == null ? "" : message);
		}
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
		final BlockEntity blockEntity = serverPlayer.level().getBlockEntity(blockPos);
		if (blockEntity instanceof AbstractStandingSignBlock.StandingSignBlockEntity standingSignBlockEntity) {
			standingSignBlockEntity.setMessages(messages);
		}
	}
}
