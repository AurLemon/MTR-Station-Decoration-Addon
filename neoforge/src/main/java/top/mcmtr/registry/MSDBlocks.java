package top.mcmtr.registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mtr.block.BlockNode;
import top.mcmtr.block.DecorationCeilingBlock;
import top.mcmtr.block.DecorationCeilingLightBlock;
import top.mcmtr.block.DecorationBookBlock;
import top.mcmtr.block.DecorationFloorBlock;
import top.mcmtr.block.DecorationPCBlock;
import top.mcmtr.block.DecorationStairBlock;
import top.mcmtr.block.BlockCatenaryWithModel;
import top.mcmtr.block.Yamanote5PIDSBlock;
import top.mcmtr.block.Yamanote6PIDSBlock;
import top.mcmtr.block.Yamanote7PIDSBlock;
import top.mcmtr.block.YamanoteRailwaySignBlock;
import top.mcmtr.block.YamanoteRailwaySignPoleBlock;
import top.mcmtr.block.DisplayBoardHorizontalBlock;
import top.mcmtr.block.DisplayBoardVerticalBlock;
import top.mcmtr.block.HallSeatBlock;
import top.mcmtr.block.OldDecorationRackBlock;
import top.mcmtr.block.OldElectricPoleBlock;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.block.RailingStairBlock;
import top.mcmtr.block.RigidCatenaryNodeBlock;
import top.mcmtr.block.StandingSignPoleBlock;
import top.mcmtr.block.StandingSign1Block;
import top.mcmtr.block.StandingSignBlock;
import top.mcmtr.block.SurveillanceCamerasBlock;
import top.mcmtr.block.Yamanote4PIDSBlock;
import top.mcmtr.block.YuuniPIDSPoleBlock;
import top.mcmtr.block.YuuniPIDSBlock;
import top.mcmtr.block.YuuniTicketBlock;
import top.mcmtr.init.MSDNeoForge;

public final class MSDBlocks {

	private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MSDNeoForge.MOD_ID);
	private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MSDNeoForge.MOD_ID);
	private static final Map<String, DeferredItem<BlockItem>> BLOCK_ITEMS = new LinkedHashMap<>();

	public static final DeferredBlock<Block> YUUNI_PIDS =
			register("yuuni_pids", () -> new YuuniPIDSBlock(baseProperties().noOcclusion(), 2, () -> MSDBlockEntities.YUUNI_PIDS.get()));
	public static final DeferredBlock<Block> YUUNI_2_PIDS =
			register("yuuni_2_pids", () -> new YuuniPIDSBlock(baseProperties().noOcclusion(), 1, () -> MSDBlockEntities.YUUNI_2_PIDS.get()));
	public static final DeferredBlock<Block> YUUNI_PIDS_POLE =
			register("yuuni_pids_pole", () -> new YuuniPIDSPoleBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_2_EVEN =
			register("yamanote_railway_sign_2_even", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 2, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_2_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_2_ODD =
			register("yamanote_railway_sign_2_odd", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 2, true, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_2_ODD.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_3_EVEN =
			register("yamanote_railway_sign_3_even", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 3, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_3_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_3_ODD =
			register("yamanote_railway_sign_3_odd", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 3, true, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_3_ODD.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_4_EVEN =
			register("yamanote_railway_sign_4_even", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 4, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_4_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_4_ODD =
			register("yamanote_railway_sign_4_odd", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 4, true, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_4_ODD.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_5_EVEN =
			register("yamanote_railway_sign_5_even", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 5, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_5_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_5_ODD =
			register("yamanote_railway_sign_5_odd", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 5, true, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_5_ODD.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_6_EVEN =
			register("yamanote_railway_sign_6_even", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 6, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_6_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_6_ODD =
			register("yamanote_railway_sign_6_odd", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 6, true, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_6_ODD.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_7_EVEN =
			register("yamanote_railway_sign_7_even", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 7, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_7_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_7_ODD =
			register("yamanote_railway_sign_7_odd", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 7, true, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_7_ODD.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_MIDDLE =
			register("yamanote_railway_sign_middle", () -> new YamanoteRailwaySignBlock(baseProperties().noOcclusion().lightLevel(state -> 15), 0, false, () -> MSDBlockEntities.YAMANOTE_RAILWAY_SIGN_2_EVEN.get()));
	public static final DeferredBlock<Block> YAMANOTE_RAILWAY_SIGN_POLE =
			register("yamanote_railway_sign_pole", () -> new YamanoteRailwaySignPoleBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> YAMANOTE_4_PIDS =
			register("yamanote_4_pids", () -> new Yamanote4PIDSBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.YAMANOTE_4_PIDS.get()));
	public static final DeferredBlock<Block> YAMANOTE_5_PIDS =
			register("yamanote_5_pids", () -> new Yamanote5PIDSBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.YAMANOTE_5_PIDS.get()));
	public static final DeferredBlock<Block> YAMANOTE_6_PIDS =
			register("yamanote_6_pids", () -> new Yamanote6PIDSBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.YAMANOTE_6_PIDS.get()));
	public static final DeferredBlock<Block> YAMANOTE_7_PIDS =
			register("yamanote_7_pids", () -> new Yamanote7PIDSBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.YAMANOTE_7_PIDS.get()));
	public static final DeferredBlock<Block> DISPLAY_BOARD_HORIZONTAL =
			register("display_board_horizontal", () -> new DisplayBoardHorizontalBlock(baseProperties().noOcclusion().lightLevel(state -> 5)));
	public static final DeferredBlock<Block> DISPLAY_BOARD_VERTICAL =
			register("display_board_vertical", () -> new DisplayBoardVerticalBlock(baseProperties().noOcclusion().lightLevel(state -> 5)));
	public static final DeferredBlock<Block> NEW_CATENARY_NODE =
			register("new_catenary_node", () -> new BlockNode.BlockContinuousMovementNode(baseProperties().noOcclusion(), false, true));
	public static final DeferredBlock<Block> RIGID_CATENARY_NODE =
			register("rigid_catenary_node", () -> new RigidCatenaryNodeBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_WITH_LONG =
			register("catenary_with_long", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG, () -> MSDBlockEntities.CATENARY_WITH_LONG.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_LONG_TOP =
			register("catenary_with_long_top", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG_TOP, () -> MSDBlockEntities.CATENARY_WITH_LONG_TOP.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_SHORT =
			register("catenary_with_short", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT, () -> MSDBlockEntities.CATENARY_WITH_SHORT.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_SHORT_TOP =
			register("catenary_with_short_top", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT_TOP, () -> MSDBlockEntities.CATENARY_WITH_SHORT_TOP.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_LONG_COUNTERWEIGHT =
			register("catenary_with_long_counterweight", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG_COUNTERWEIGHT, () -> MSDBlockEntities.CATENARY_WITH_LONG_COUNTERWEIGHT.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR =
			register("catenary_with_long_counterweight_mirror", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG_COUNTERWEIGHT_MIRROR, () -> MSDBlockEntities.CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_SHORT_COUNTERWEIGHT =
			register("catenary_with_short_counterweight", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT_COUNTERWEIGHT, () -> MSDBlockEntities.CATENARY_WITH_SHORT_COUNTERWEIGHT.get()));
	public static final DeferredBlock<Block> CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR =
			register("catenary_with_short_counterweight_mirror", () -> new BlockCatenaryWithModel(baseProperties().noOcclusion(), BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT_COUNTERWEIGHT_MIRROR, () -> MSDBlockEntities.CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR.get()));
	public static final DeferredBlock<Block> YUUNI_STANDING_SIGN =
			register("yuuni_standing_sign", () -> new StandingSignBlock(baseProperties().noOcclusion().lightLevel(state -> 8), () -> MSDBlockEntities.YUUNI_STANDING_SIGN.get()));
	public static final DeferredBlock<Block> YUUNI_STANDING_SIGN_1 =
			register("yuuni_standing_sign_1", () -> new StandingSign1Block(baseProperties().noOcclusion().lightLevel(state -> 8), () -> MSDBlockEntities.YUUNI_STANDING_SIGN_1.get()));
	public static final DeferredBlock<Block> YUUNI_TICKET =
			register("yuuni_ticket", () -> new YuuniTicketBlock(baseProperties().noOcclusion().lightLevel(state -> 5)));
	public static final DeferredBlock<Block> YUUNI_STANDING_SIGN_POLE =
			register("yuuni_standing_sign_pole", () -> new StandingSignPoleBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> SURVEILLANCE_CAMERAS =
			register("surveillance_cameras", () -> new SurveillanceCamerasBlock(baseProperties().noOcclusion().lightLevel(state -> 5)));
	public static final DeferredBlock<Block> SURVEILLANCE_CAMERAS_WALL =
			register("surveillance_cameras_wall", () -> new SurveillanceCamerasBlock(baseProperties().noOcclusion().lightLevel(state -> 5)));
	public static final DeferredBlock<Block> HALL_SEAT_MIDDLE =
			register("hall_seat_middle", () -> new HallSeatBlock(baseProperties()));
	public static final DeferredBlock<Block> HALL_SEAT_SIDE =
			register("hall_seat_side", () -> new HallSeatBlock(baseProperties()));
	public static final DeferredBlock<Block> HALL_SEAT_SIDE_MIRROR =
			register("hall_seat_side_mirror", () -> new HallSeatBlock(baseProperties()));
	public static final DeferredBlock<Block> DECORATION_CEILING =
			register("decoration_ceiling", () -> new DecorationCeilingBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> DECORATION_CEILING_LIGHT =
			register("decoration_ceiling_light", () -> new DecorationCeilingLightBlock(baseProperties().noOcclusion().lightLevel(state -> 15)));
	public static final DeferredBlock<Block> DECORATION_BOOK =
			register("decoration_book", () -> new DecorationBookBlock(baseProperties().noOcclusion().lightLevel(state -> 2)));
	public static final DeferredBlock<Block> DECORATION_FLOOR =
			register("decoration_floor", () -> new DecorationFloorBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> DECORATION_PC =
			register("decoration_pc", () -> new DecorationPCBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> DECORATION_STAIR =
			register("decoration_stair", () -> new DecorationStairBlock(baseProperties()));
	public static final DeferredBlock<Block> RAILING_STAIR_START =
			register("railing_stair_start", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 1));
	public static final DeferredBlock<Block> RAILING_STAIR =
			register("railing_stair", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 2));
	public static final DeferredBlock<Block> RAILING_STAIR_END =
			register("railing_stair_end", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 3));
	public static final DeferredBlock<Block> RAILING_STAIR_CORNER =
			register("railing_stair_corner", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 4));
	public static final DeferredBlock<Block> RAILING_STAIR_CORNER_2 =
			register("railing_stair_corner_2", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 5));
	public static final DeferredBlock<Block> RAILING_STAIR_START_MIRROR =
			register("railing_stair_start_mirror", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 1));
	public static final DeferredBlock<Block> RAILING_STAIR_MIRROR =
			register("railing_stair_mirror", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 2));
	public static final DeferredBlock<Block> RAILING_STAIR_END_MIRROR =
			register("railing_stair_end_mirror", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 3));
	public static final DeferredBlock<Block> RAILING_STAIR_CORNER_MIRROR =
			register("railing_stair_corner_mirror", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 4));
	public static final DeferredBlock<Block> RAILING_STAIR_CORNER_MIRROR_2 =
			register("railing_stair_corner_mirror_2", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 5));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_1 =
			register("railing_stair_glass_1", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 1));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_2 =
			register("railing_stair_glass_2", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 2));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_3 =
			register("railing_stair_glass_3", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 3));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_4 =
			register("railing_stair_glass_4", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 4));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_5 =
			register("railing_stair_glass_5", () -> new RailingStairBlock(baseProperties().noOcclusion(), false, 5));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_MIRROR_1 =
			register("railing_stair_glass_mirror_1", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 1));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_MIRROR_2 =
			register("railing_stair_glass_mirror_2", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 2));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_MIRROR_3 =
			register("railing_stair_glass_mirror_3", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 3));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_MIRROR_4 =
			register("railing_stair_glass_mirror_4", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 4));
	public static final DeferredBlock<Block> RAILING_STAIR_GLASS_MIRROR_5 =
			register("railing_stair_glass_mirror_5", () -> new RailingStairBlock(baseProperties().noOcclusion(), true, 5));
	public static final DeferredBlock<Block> CATENARY_POLE =
			register("catenary_pole", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_POLE_TOP_MIDDLE =
			register("catenary_pole_top_middle", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_POLE_TOP_SIDE =
			register("catenary_pole_top_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> SHORT_CATENARY_RACK_BOTH_SIDE =
			register("short_catenary_rack_both_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> SHORT_CATENARY_RACK_SIDE =
			register("short_catenary_rack_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> SHORT_CATENARY_RACK_POLE_BOTH_SIDE =
			register("short_catenary_rack_pole_both_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> SHORT_CATENARY_RACK_POLE =
			register("short_catenary_rack_pole", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> SHORT_CATENARY_RACK =
			register("short_catenary_rack", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_RACK_BOTH_SIDE =
			register("catenary_rack_both_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_RACK_SIDE =
			register("catenary_rack_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_RACK_POLE_BOTH_SIDE =
			register("catenary_rack_pole_both_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_RACK_POLE =
			register("catenary_rack_pole", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_RACK_2 =
			register("catenary_rack_2", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> CATENARY_RACK_1 =
			register("catenary_rack_1", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> ELECTRIC_POLE_SIDE =
			register("electric_pole_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> ELECTRIC_POLE_ANOTHER_SIDE =
			register("electric_pole_another_side", () -> new OldDecorationRackBlock(baseProperties().noOcclusion()));
	public static final DeferredBlock<Block> ELECTRIC_POLE_TOP_SIDE =
			register("electric_pole_top_side", () -> new OldElectricPoleBlock(baseProperties().noOcclusion(), false));
	public static final DeferredBlock<Block> ELECTRIC_POLE_TOP_BOTH_SIDE =
			register("electric_pole_top_both_side", () -> new OldElectricPoleBlock(baseProperties().noOcclusion(), true));
	public static final DeferredBlock<Block> CATENARY_NODE =
			register("catenary_node", () -> new OldNodeBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.CATENARY_NODE.get()));
	public static final DeferredBlock<Block> CATENARY_NODE_STYLE_2 =
			register("catenary_node_style_2", () -> new OldNodeBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.CATENARY_NODE_STYLE_2.get()));
	public static final DeferredBlock<Block> SHORT_CATENARY_NODE =
			register("short_catenary_node", () -> new OldNodeBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.SHORT_CATENARY_NODE.get()));
	public static final DeferredBlock<Block> SHORT_CATENARY_NODE_STYLE_2 =
			register("short_catenary_node_style_2", () -> new OldNodeBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.SHORT_CATENARY_NODE_STYLE_2.get()));
	public static final DeferredBlock<Block> ELECTRIC_NODE =
			register("electric_node", () -> new OldNodeBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.ELECTRIC_NODE.get()));
	public static final DeferredBlock<Block> TRANS_CATENARY_NODE =
			register("trans_catenary_node", () -> new OldNodeBlock(baseProperties().noOcclusion(), () -> MSDBlockEntities.TRANS_CATENARY_NODE.get()));

	private MSDBlocks() {
	}

	public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
	}

	public static void addCreativeTabItems(Consumer<Item> consumer) {
		BLOCK_ITEMS.values().forEach(item -> consumer.accept(item.get()));
	}

	private static DeferredBlock<Block> register(String name, Supplier<? extends Block> supplier) {
		final DeferredBlock<Block> block = BLOCKS.register(name, supplier);
		BLOCK_ITEMS.put(name, ITEMS.registerSimpleBlockItem(name, block));
		return block;
	}

	private static BlockBehaviour.Properties baseProperties() {
		return BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.METAL);
	}
}
