package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import top.mcmtr.block.OldNodeBlock;

public final class MSDPacketUpdateCatenaryNode extends PacketHandler {

	private final BlockPos blockPos;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;

	public MSDPacketUpdateCatenaryNode(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
		offsetX = packetBufferReceiver.readDouble();
		offsetY = packetBufferReceiver.readDouble();
		offsetZ = packetBufferReceiver.readDouble();
	}

	public MSDPacketUpdateCatenaryNode(BlockPos blockPos, double offsetX, double offsetY, double offsetZ) {
		this.blockPos = blockPos;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeDouble(offsetX);
		packetBufferSender.writeDouble(offsetY);
		packetBufferSender.writeDouble(offsetZ);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
		final BlockEntity blockEntity = serverPlayer.level().getBlockEntity(blockPos);
		if (blockEntity instanceof OldNodeBlock.OldNodeBlockEntity oldNodeBlockEntity) {
			oldNodeBlockEntity.setOffsetPosition(offsetX, offsetY, offsetZ);
		}
	}
}
