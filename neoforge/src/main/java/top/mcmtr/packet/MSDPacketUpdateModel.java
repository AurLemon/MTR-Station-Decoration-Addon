package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import top.mcmtr.block.BlockCatenaryWithModel;

public final class MSDPacketUpdateModel extends PacketHandler {

	private final BlockPos blockPos;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;
	private final double rotationY;

	public MSDPacketUpdateModel(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
		offsetX = packetBufferReceiver.readDouble();
		offsetY = packetBufferReceiver.readDouble();
		offsetZ = packetBufferReceiver.readDouble();
		rotationY = packetBufferReceiver.readDouble();
	}

	public MSDPacketUpdateModel(BlockPos blockPos, double offsetX, double offsetY, double offsetZ, double rotationY) {
		this.blockPos = blockPos;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.rotationY = rotationY;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeDouble(offsetX);
		packetBufferSender.writeDouble(offsetY);
		packetBufferSender.writeDouble(offsetZ);
		packetBufferSender.writeDouble(rotationY);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
		final BlockEntity blockEntity = serverPlayer.level().getBlockEntity(blockPos);
		if (blockEntity instanceof BlockCatenaryWithModel.BlockCatenaryWithModelEntity catenaryWithModelEntity) {
			catenaryWithModelEntity.setTransform(offsetX, offsetY, offsetZ, 0, rotationY, 0);
		}
	}
}
