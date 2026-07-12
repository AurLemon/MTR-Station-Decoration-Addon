package top.mcmtr.packet;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Utilities;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import org.mtr.packet.PacketHandler;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.operation.MSDUpdateDataRequest;
import top.mcmtr.init.MSDNeoForgeRuntime;

public final class MSDPacketUpdateRigidCatenary extends PacketHandler {

	private final String content;

	public MSDPacketUpdateRigidCatenary(PacketBufferReceiver packetBufferReceiver) {
		content = packetBufferReceiver.readString();
	}

	public MSDPacketUpdateRigidCatenary(RigidCatenary rigidCatenary) {
		content = Utilities.getJsonObjectFromData(rigidCatenary).toString();
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeString(content);
	}

	@Override
	public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayer) {
		final MSDNeoForgeRuntime runtime = MSDNeoForgeRuntime.getInstance();
		if (runtime == null) {
			return;
		}

		final RigidCatenary rigidCatenary = new RigidCatenary(new JsonReader(Utilities.parseJson(content)));
		runtime.sendUpdate(serverPlayer.serverLevel(), new MSDUpdateDataRequest(runtime.newClientData()).addRigidCatenary(rigidCatenary));
	}
}
