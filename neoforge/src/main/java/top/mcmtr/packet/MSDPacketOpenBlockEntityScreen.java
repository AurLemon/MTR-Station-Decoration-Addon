package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;

public final class MSDPacketOpenBlockEntityScreen extends PacketHandler {

	private final BlockPos blockPos;

	public MSDPacketOpenBlockEntityScreen(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
	}

	public MSDPacketOpenBlockEntityScreen(BlockPos blockPos) {
		this.blockPos = blockPos;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
	}

	@Override
	public void runClient() {
		MSDClientPacketHelper.openYamanoteRailwaySignScreen(blockPos);
	}
}
