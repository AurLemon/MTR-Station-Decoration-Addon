package top.mcmtr.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.mtr.registry.RegistryServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mcmtr.command.MSDProbeBlockEntityCommand;
import top.mcmtr.command.MSDProbeCatenaryCommand;
import top.mcmtr.command.MSDProbeCustomTextCommand;
import top.mcmtr.command.MSDProbeRigidCommand;
import top.mcmtr.packet.MSDPacketOpenBlockEntityScreen;
import top.mcmtr.packet.MSDPacketOpenCatenaryScreen;
import top.mcmtr.packet.MSDPacketOpenCatenaryWithModelScreen;
import top.mcmtr.packet.MSDPacketOpenCustomScreen;
import top.mcmtr.packet.MSDPacketOpenRigidCatenaryShapeScreen;
import top.mcmtr.packet.MSDPacketUpdateCatenaryNode;
import top.mcmtr.packet.MSDPacketUpdateCustomText;
import top.mcmtr.packet.MSDPacketUpdateRigidCatenary;
import top.mcmtr.packet.MSDPacketUpdateModel;
import top.mcmtr.packet.MSDPacketUpdateYamanoteRailwaySignConfig;
import top.mcmtr.registry.MSDBlockEntities;
import top.mcmtr.registry.MSDBlocks;
import top.mcmtr.registry.MSDCreativeTabs;
import top.mcmtr.registry.MSDItems;

@Mod(MSDNeoForge.MOD_ID)
public class MSDNeoForge {

	public static final String MOD_ID = "msd";
	private static final Logger LOGGER = LoggerFactory.getLogger("MTR Station Decoration Addon");

	public MSDNeoForge(IEventBus modEventBus) {
		MSDBlocks.register(modEventBus);
		MSDBlockEntities.register(modEventBus);
		MSDItems.register(modEventBus);
		MSDCreativeTabs.register(modEventBus);
		new MSDNeoForgeRuntime();
		NeoForge.EVENT_BUS.addListener(MSDProbeBlockEntityCommand::register);
		NeoForge.EVENT_BUS.addListener(MSDProbeCatenaryCommand::register);
		NeoForge.EVENT_BUS.addListener(MSDProbeCustomTextCommand::register);
		NeoForge.EVENT_BUS.addListener(MSDProbeRigidCommand::register);
		RegistryServer.registerPacket(MSDPacketOpenBlockEntityScreen.class, MSDPacketOpenBlockEntityScreen::new);
		RegistryServer.registerPacket(MSDPacketOpenCatenaryScreen.class, MSDPacketOpenCatenaryScreen::new);
		RegistryServer.registerPacket(MSDPacketOpenCatenaryWithModelScreen.class, MSDPacketOpenCatenaryWithModelScreen::new);
		RegistryServer.registerPacket(MSDPacketOpenCustomScreen.class, MSDPacketOpenCustomScreen::new);
		RegistryServer.registerPacket(MSDPacketOpenRigidCatenaryShapeScreen.class, MSDPacketOpenRigidCatenaryShapeScreen::new);
		RegistryServer.registerPacket(MSDPacketUpdateCatenaryNode.class, MSDPacketUpdateCatenaryNode::new);
		RegistryServer.registerPacket(MSDPacketUpdateCustomText.class, MSDPacketUpdateCustomText::new);
		RegistryServer.registerPacket(MSDPacketUpdateRigidCatenary.class, MSDPacketUpdateRigidCatenary::new);
		RegistryServer.registerPacket(MSDPacketUpdateModel.class, MSDPacketUpdateModel::new);
		RegistryServer.registerPacket(MSDPacketUpdateYamanoteRailwaySignConfig.class, MSDPacketUpdateYamanoteRailwaySignConfig::new);
		LOGGER.info("MTR Station Decoration Addon NeoForge blocks registered");
	}
}
