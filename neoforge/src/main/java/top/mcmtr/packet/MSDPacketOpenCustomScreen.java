package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;

public final class MSDPacketOpenCustomScreen extends PacketHandler {

	private final BlockPos blockPos;
	private final int maxArrivals;

	public MSDPacketOpenCustomScreen(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
		maxArrivals = packetBufferReceiver.readInt();
	}

	public MSDPacketOpenCustomScreen(BlockPos blockPos, int maxArrivals) {
		this.blockPos = blockPos;
		this.maxArrivals = maxArrivals;
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeInt(maxArrivals);
	}

	@Override
	public void runClient() {
		MSDClientPacketHelper.openCustomTextScreen(blockPos, maxArrivals);
	}
}
