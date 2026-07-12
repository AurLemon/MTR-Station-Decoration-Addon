package top.mcmtr.packet;

import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Utilities;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import org.mtr.packet.PacketHandler;
import top.mcmtr.core.data.RigidCatenary;

public final class MSDPacketOpenRigidCatenaryShapeScreen extends PacketHandler {

	private final String content;

	public MSDPacketOpenRigidCatenaryShapeScreen(PacketBufferReceiver packetBufferReceiver) {
		content = packetBufferReceiver.readString();
	}

	public MSDPacketOpenRigidCatenaryShapeScreen(RigidCatenary rigidCatenary) {
		content = Utilities.getJsonObjectFromData(rigidCatenary).toString();
	}

	@Override
	public void write(PacketBufferSender packetBufferSender) {
		packetBufferSender.writeString(content);
	}

	@Override
	public void runClient() {
		MSDClientPacketHelper.openRigidCatenaryShapeScreen(new RigidCatenary(new JsonReader(Utilities.parseJson(content))));
	}
}
