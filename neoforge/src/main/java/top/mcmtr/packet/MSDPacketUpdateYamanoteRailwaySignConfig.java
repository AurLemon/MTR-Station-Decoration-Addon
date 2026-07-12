package top.mcmtr.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import top.mcmtr.block.YamanoteRailwaySignBlock;

public final class MSDPacketUpdateYamanoteRailwaySignConfig extends PacketHandler {

	private final BlockPos blockPos;
	private final LongAVLTreeSet selectedIds;
	private final String[] signIds;

	public MSDPacketUpdateYamanoteRailwaySignConfig(PacketBufferReceiver packetBufferReceiver) {
		blockPos = BlockPos.of(packetBufferReceiver.readLong());
		selectedIds = new LongAVLTreeSet();
		final int selectedIdsLength = packetBufferReceiver.readInt();
		for (int i = 0; i < selectedIdsLength; i++) {
			selectedIds.add(packetBufferReceiver.readLong());
		}
		signIds = new String[packetBufferReceiver.readInt()];
		for (int i = 0; i < signIds.length; i++) {
			final String signId = packetBufferReceiver.readString();
			signIds[i] = signId.isEmpty() ? null : signId;
		}
	}

	public MSDPacketUpdateYamanoteRailwaySignConfig(BlockPos blockPos, LongAVLTreeSet selectedIds, String[] signIds) {
		this.blockPos = blockPos;
		this.selectedIds = new LongAVLTreeSet(selectedIds);
		this.signIds = signIds.clone();
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeLong(blockPos.asLong());
		packetBufferSender.writeInt(selectedIds.size());
		selectedIds.forEach(packetBufferSender::writeLong);
		packetBufferSender.writeInt(signIds.length);
		for (final String signId : signIds) {
			packetBufferSender.writeString(signId == null ? "" : signId);
		}
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
		final BlockEntity blockEntity = serverPlayer.level().getBlockEntity(blockPos);
		if (blockEntity instanceof YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity signBlockEntity) {
			signBlockEntity.setData(selectedIds, signIds);
		}
	}
}
