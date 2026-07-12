package top.mcmtr.init;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.mtr.render.RenderPIDS;
import org.mtr.neoforge.ModEventBusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mcmtr.registry.MSDBlockEntities;
import top.mcmtr.registry.MSDBlocks;
import top.mcmtr.registry.MSDItems;
import top.mcmtr.render.RenderCatenaryModel;
import top.mcmtr.render.RenderCustomText;
import top.mcmtr.render.RenderYamanoteRailwaySign;

@Mod(value = MSDNeoForge.MOD_ID, dist = Dist.CLIENT)
public class MSDNeoForgeClient {

	private static final Logger LOGGER = LoggerFactory.getLogger("MTR Station Decoration Addon");

	public MSDNeoForgeClient(IEventBus modEventBus) {
		modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) -> event.enqueueWork(() -> {
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.NEW_CATENARY_NODE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RIGID_CATENARY_NODE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_LONG.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_LONG_TOP.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_SHORT.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_SHORT_TOP.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_LONG_COUNTERWEIGHT.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_SHORT_COUNTERWEIGHT.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_NODE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_NODE_STYLE_2.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_NODE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_NODE_STYLE_2.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.ELECTRIC_NODE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.TRANS_CATENARY_NODE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SURVEILLANCE_CAMERAS.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SURVEILLANCE_CAMERAS_WALL.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YUUNI_PIDS.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YUUNI_2_PIDS.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YUUNI_STANDING_SIGN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YUUNI_STANDING_SIGN_1.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YUUNI_STANDING_SIGN_POLE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YUUNI_TICKET.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.HALL_SEAT_MIDDLE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.HALL_SEAT_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.HALL_SEAT_SIDE_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DECORATION_BOOK.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DECORATION_CEILING.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DECORATION_CEILING_LIGHT.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DECORATION_PC.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DECORATION_STAIR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DECORATION_FLOOR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DISPLAY_BOARD_HORIZONTAL.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.DISPLAY_BOARD_VERTICAL.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_START.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_END.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_CORNER.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_CORNER_2.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_START_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_END_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_CORNER_MIRROR.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_CORNER_MIRROR_2.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_1.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_2.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_3.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_4.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_5.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_MIRROR_1.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_MIRROR_2.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_MIRROR_3.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_MIRROR_4.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.RAILING_STAIR_GLASS_MIRROR_5.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_POLE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_POLE_TOP_MIDDLE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_POLE_TOP_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_RACK_1.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_RACK_2.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_RACK_BOTH_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_RACK_POLE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_RACK_POLE_BOTH_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.CATENARY_RACK_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_RACK.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_RACK_BOTH_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_RACK_POLE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_RACK_POLE_BOTH_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.SHORT_CATENARY_RACK_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.ELECTRIC_POLE_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.ELECTRIC_POLE_ANOTHER_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.ELECTRIC_POLE_TOP_BOTH_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.ELECTRIC_POLE_TOP_SIDE.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_2_EVEN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_2_ODD.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_3_EVEN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_3_ODD.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_4_EVEN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_4_ODD.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_5_EVEN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_5_ODD.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_6_EVEN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_6_ODD.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_7_EVEN.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_7_ODD.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(MSDBlocks.YAMANOTE_RAILWAY_SIGN_MIDDLE.get(), RenderType.cutout());

			registerSelectedPredicate(MSDItems.CATENARY_CONNECTOR.get());
			registerSelectedPredicate(MSDItems.ELECTRIC_CONNECTOR.get());
			registerSelectedPredicate(MSDItems.RIGID_SOFT_CATENARY_CONNECTOR.get());
			registerSelectedPredicate(MSDItems.RIGID_CATENARY_CONNECTOR.get());
			registerSelectedPredicate(MSDItems.CATENARY_REMOVER.get());
			registerSelectedPredicate(MSDItems.RIGID_CATENARY_REMOVER.get());

			registerHoldPredicate(MSDBlocks.SURVEILLANCE_CAMERAS.get().asItem());
			registerHoldPredicate(MSDBlocks.HALL_SEAT_MIDDLE.get().asItem());
			registerHoldPredicate(MSDBlocks.DECORATION_BOOK.get().asItem());
			registerHoldPredicate(MSDBlocks.DECORATION_CEILING.get().asItem());
			registerHoldPredicate(MSDBlocks.DECORATION_CEILING_LIGHT.get().asItem());
			registerHoldPredicate(MSDBlocks.DECORATION_FLOOR.get().asItem());
			registerHoldPredicate(MSDBlocks.DECORATION_PC.get().asItem());
			registerHoldPredicate(MSDBlocks.DECORATION_STAIR.get().asItem());
			registerHoldPredicate(MSDBlocks.DISPLAY_BOARD_HORIZONTAL.get().asItem());
			registerHoldPredicate(MSDBlocks.DISPLAY_BOARD_VERTICAL.get().asItem());
			registerHoldPredicate(MSDBlocks.RAILING_STAIR_START.get().asItem());
			registerHoldPredicate(MSDBlocks.RAILING_STAIR_START_MIRROR.get().asItem());
			registerHoldPredicate(MSDBlocks.RAILING_STAIR_GLASS_1.get().asItem());
			registerHoldPredicate(MSDBlocks.RAILING_STAIR_GLASS_MIRROR_1.get().asItem());
			registerHoldPredicate(MSDBlocks.YUUNI_STANDING_SIGN.get().asItem());
			registerHoldPredicate(MSDBlocks.YUUNI_STANDING_SIGN_1.get().asItem());
			registerHoldPredicate(MSDBlocks.YUUNI_STANDING_SIGN_POLE.get().asItem());
		}));
		ModEventBusClient.BLOCK_ENTITY_RENDERERS.add(event -> {
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_2_EVEN.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_2_ODD.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_3_EVEN.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_3_ODD.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_4_EVEN.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_4_ODD.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_5_EVEN.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_5_ODD.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_6_EVEN.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_6_ODD.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_7_EVEN.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_7_ODD.get(), context -> new RenderYamanoteRailwaySign());
			event.registerBlockEntityRenderer(MSDBlockEntities.YUUNI_STANDING_SIGN.get(), context -> new RenderCustomText(3, 8F, 14.5F, 7.01F, 15F, 11F, true, 2F, 3.1F, 6.2F, 0.004F, 0xFFFFFF, 0x000000, 0x000000));
			event.registerBlockEntityRenderer(MSDBlockEntities.YUUNI_STANDING_SIGN_1.get(), context -> new RenderCustomText(1, 2.5F, 9.25F, 7.65F, 4F, 11F, true, 1.6F, 1.6F, 3.2F, 0.0625F, 0xFFFFFF));
			event.registerBlockEntityRenderer(MSDBlockEntities.YUUNI_PIDS.get(), context -> new RenderPIDS<>(2.5F, 7.5F, 6F, 6.5F, 27, true, 1.25F));
			event.registerBlockEntityRenderer(MSDBlockEntities.YUUNI_2_PIDS.get(), context -> new RenderPIDS<>(4F, 7.5F, 5.9F, 2.5F, 24, true, 1F));
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_4_PIDS.get(), context -> new RenderPIDS<>(0F, 15F, 7F, 6F, 32, true, 1F));
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_5_PIDS.get(), context -> new RenderPIDS<>(-4F, 15F, 7F, 6F, 40, true, 1F));
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_6_PIDS.get(), context -> new RenderPIDS<>(-8F, 15F, 7F, 6F, 48, true, 1F));
			event.registerBlockEntityRenderer(MSDBlockEntities.YAMANOTE_7_PIDS.get(), context -> new RenderPIDS<>(-12F, 15F, 7F, 6F, 56, true, 1F));
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_LONG.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_LONG_TOP.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_SHORT.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_SHORT_TOP.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_LONG_COUNTERWEIGHT.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_SHORT_COUNTERWEIGHT.get(), context -> new RenderCatenaryModel());
			event.registerBlockEntityRenderer(MSDBlockEntities.CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR.get(), context -> new RenderCatenaryModel());
		});
		LOGGER.info("MTR Station Decoration Addon NeoForge client initialized");
	}

	private static void registerSelectedPredicate(net.minecraft.world.item.Item item) {
		ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(MSDNeoForge.MOD_ID, "selected"), (itemStack, clientLevel, livingEntity, seed) ->
				itemStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY)
						.copyTag()
						.contains("catenary_pos") ? 1 : 0);
	}

	private static void registerHoldPredicate(net.minecraft.world.item.Item item) {
		ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(MSDNeoForge.MOD_ID, "hold"), (itemStack, clientLevel, livingEntity, seed) -> {
			final net.minecraft.nbt.CompoundTag tag = itemStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
			return tag.contains("hold_num") ? tag.getInt("hold_num") / 10F : 0;
		});
	}
}
