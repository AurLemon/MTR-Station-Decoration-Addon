package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;

public final class MSDPacketOpenCatenaryScreen extends PacketHandler {

	private final BlockPos blockPos;
	private final boolean isConnected;

	public MSDPacketOpenCatenaryScreen(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
		isConnected = packetBufferReceiver.readBoolean();
	}

	public MSDPacketOpenCatenaryScreen(BlockPos blockPos, boolean isConnected) {
		this.blockPos = blockPos;
		this.isConnected = isConnected;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeBoolean(isConnected);
	}

	@Override
	public void runClient() {
		MSDClientPacketHelper.openCatenaryScreen(isConnected, blockPos);
	}
}
