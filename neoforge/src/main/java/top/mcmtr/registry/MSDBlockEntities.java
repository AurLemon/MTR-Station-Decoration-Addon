package top.mcmtr.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.mcmtr.block.AbstractYamanotePIDSBlock;
import top.mcmtr.block.BlockCatenaryWithModel;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.block.RigidCatenaryNodeBlock;
import top.mcmtr.block.AbstractStandingSignBlock;
import top.mcmtr.block.YamanoteRailwaySignBlock;
import top.mcmtr.block.Yamanote4PIDSBlock;
import top.mcmtr.block.YuuniPIDSBlock;
import top.mcmtr.init.MSDNeoForge;

public final class MSDBlockEntities {

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
			DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MSDNeoForge.MOD_ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YuuniPIDSBlock.YuuniPIDSBlockEntity>> YUUNI_PIDS =
			register("yuuni_pids", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YuuniPIDSBlock.YuuniPIDSBlockEntity(2, getYuuniPidsType(), pos, state),
					MSDBlocks.YUUNI_PIDS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YuuniPIDSBlock.YuuniPIDSBlockEntity>> YUUNI_2_PIDS =
			register("yuuni_2_pids", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YuuniPIDSBlock.YuuniPIDSBlockEntity(1, getYuuni2PidsType(), pos, state),
					MSDBlocks.YUUNI_2_PIDS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<Yamanote4PIDSBlock.Yamanote4PIDSBlockEntity>> YAMANOTE_4_PIDS =
			register("yamanote_4_pids", () -> BlockEntityType.Builder.of(
					(pos, state) -> new Yamanote4PIDSBlock.Yamanote4PIDSBlockEntity(getYamanote4PidsType(), pos, state),
					MSDBlocks.YAMANOTE_4_PIDS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RigidCatenaryNodeBlock.RigidCatenaryNodeBlockEntity>> RIGID_CATENARY_NODE =
			register("rigid_catenary_node", () -> BlockEntityType.Builder.of(
					(pos, state) -> new RigidCatenaryNodeBlock.RigidCatenaryNodeBlockEntity(getRigidCatenaryNodeType(), pos, state),
					MSDBlocks.RIGID_CATENARY_NODE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_2_EVEN =
			register("yamanote_railway_sign_2_even", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(2, false, getYamanoteRailwaySign2EvenType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_2_EVEN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_2_ODD =
			register("yamanote_railway_sign_2_odd", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(2, true, getYamanoteRailwaySign2OddType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_2_ODD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_3_EVEN =
			register("yamanote_railway_sign_3_even", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(3, false, getYamanoteRailwaySign3EvenType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_3_EVEN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_3_ODD =
			register("yamanote_railway_sign_3_odd", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(3, true, getYamanoteRailwaySign3OddType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_3_ODD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_4_EVEN =
			register("yamanote_railway_sign_4_even", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(4, false, getYamanoteRailwaySign4EvenType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_4_EVEN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_4_ODD =
			register("yamanote_railway_sign_4_odd", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(4, true, getYamanoteRailwaySign4OddType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_4_ODD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_5_EVEN =
			register("yamanote_railway_sign_5_even", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(5, false, getYamanoteRailwaySign5EvenType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_5_EVEN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_5_ODD =
			register("yamanote_railway_sign_5_odd", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(5, true, getYamanoteRailwaySign5OddType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_5_ODD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_6_EVEN =
			register("yamanote_railway_sign_6_even", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(6, false, getYamanoteRailwaySign6EvenType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_6_EVEN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_6_ODD =
			register("yamanote_railway_sign_6_odd", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(6, true, getYamanoteRailwaySign6OddType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_6_ODD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_7_EVEN =
			register("yamanote_railway_sign_7_even", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(7, false, getYamanoteRailwaySign7EvenType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_7_EVEN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity>> YAMANOTE_RAILWAY_SIGN_7_ODD =
			register("yamanote_railway_sign_7_odd", () -> BlockEntityType.Builder.of(
					(pos, state) -> new YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity(7, true, getYamanoteRailwaySign7OddType(), pos, state),
					MSDBlocks.YAMANOTE_RAILWAY_SIGN_7_ODD.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_LONG =
			register("catenary_with_long", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG, getCatenaryWithLongType(), pos, state),
					MSDBlocks.CATENARY_WITH_LONG.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_LONG_TOP =
			register("catenary_with_long_top", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG_TOP, getCatenaryWithLongTopType(), pos, state),
					MSDBlocks.CATENARY_WITH_LONG_TOP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_SHORT =
			register("catenary_with_short", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT, getCatenaryWithShortType(), pos, state),
					MSDBlocks.CATENARY_WITH_SHORT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_SHORT_TOP =
			register("catenary_with_short_top", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT_TOP, getCatenaryWithShortTopType(), pos, state),
					MSDBlocks.CATENARY_WITH_SHORT_TOP.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_LONG_COUNTERWEIGHT =
			register("catenary_with_long_counterweight", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG_COUNTERWEIGHT, getCatenaryWithLongCounterweightType(), pos, state),
					MSDBlocks.CATENARY_WITH_LONG_COUNTERWEIGHT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR =
			register("catenary_with_long_counterweight_mirror", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_LONG_COUNTERWEIGHT_MIRROR, getCatenaryWithLongCounterweightMirrorType(), pos, state),
					MSDBlocks.CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_SHORT_COUNTERWEIGHT =
			register("catenary_with_short_counterweight", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT_COUNTERWEIGHT, getCatenaryWithShortCounterweightType(), pos, state),
					MSDBlocks.CATENARY_WITH_SHORT_COUNTERWEIGHT.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity>> CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR =
			register("catenary_with_short_counterweight_mirror", () -> BlockEntityType.Builder.of(
					(pos, state) -> new BlockCatenaryWithModel.BlockCatenaryWithModelEntity(BlockCatenaryWithModel.CatenaryModel.CATENARY_SHORT_COUNTERWEIGHT_MIRROR, getCatenaryWithShortCounterweightMirrorType(), pos, state),
					MSDBlocks.CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity>> YAMANOTE_5_PIDS =
			register("yamanote_5_pids", () -> BlockEntityType.Builder.of(
					(pos, state) -> new AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity(getYamanote5PidsType(), pos, state),
					MSDBlocks.YAMANOTE_5_PIDS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity>> YAMANOTE_6_PIDS =
			register("yamanote_6_pids", () -> BlockEntityType.Builder.of(
					(pos, state) -> new AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity(getYamanote6PidsType(), pos, state),
					MSDBlocks.YAMANOTE_6_PIDS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity>> YAMANOTE_7_PIDS =
			register("yamanote_7_pids", () -> BlockEntityType.Builder.of(
					(pos, state) -> new AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity(getYamanote7PidsType(), pos, state),
					MSDBlocks.YAMANOTE_7_PIDS.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractStandingSignBlock.StandingSignBlockEntity>> YUUNI_STANDING_SIGN =
			register("yuuni_standing_sign", () -> BlockEntityType.Builder.of(
					(pos, state) -> new AbstractStandingSignBlock.StandingSignBlockEntity(3, getYuuniStandingSignType(), pos, state),
					MSDBlocks.YUUNI_STANDING_SIGN.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbstractStandingSignBlock.StandingSignBlockEntity>> YUUNI_STANDING_SIGN_1 =
			register("yuuni_standing_sign_1", () -> BlockEntityType.Builder.of(
					(pos, state) -> new AbstractStandingSignBlock.StandingSignBlockEntity(1, getYuuniStandingSign1Type(), pos, state),
					MSDBlocks.YUUNI_STANDING_SIGN_1.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldNodeBlock.OldNodeBlockEntity>> CATENARY_NODE =
			register("catenary_node", () -> BlockEntityType.Builder.of(
					(pos, state) -> new OldNodeBlock.OldNodeBlockEntity(getCatenaryNodeType(), pos, state),
					MSDBlocks.CATENARY_NODE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldNodeBlock.OldNodeBlockEntity>> CATENARY_NODE_STYLE_2 =
			register("catenary_node_style_2", () -> BlockEntityType.Builder.of(
					(pos, state) -> new OldNodeBlock.OldNodeBlockEntity(getCatenaryNodeStyle2Type(), pos, state),
					MSDBlocks.CATENARY_NODE_STYLE_2.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldNodeBlock.OldNodeBlockEntity>> SHORT_CATENARY_NODE =
			register("short_catenary_node", () -> BlockEntityType.Builder.of(
					(pos, state) -> new OldNodeBlock.OldNodeBlockEntity(getShortCatenaryNodeType(), pos, state),
					MSDBlocks.SHORT_CATENARY_NODE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldNodeBlock.OldNodeBlockEntity>> SHORT_CATENARY_NODE_STYLE_2 =
			register("short_catenary_node_style_2", () -> BlockEntityType.Builder.of(
					(pos, state) -> new OldNodeBlock.OldNodeBlockEntity(getShortCatenaryNodeStyle2Type(), pos, state),
					MSDBlocks.SHORT_CATENARY_NODE_STYLE_2.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldNodeBlock.OldNodeBlockEntity>> ELECTRIC_NODE =
			register("electric_node", () -> BlockEntityType.Builder.of(
					(pos, state) -> new OldNodeBlock.OldNodeBlockEntity(getElectricNodeType(), pos, state),
					MSDBlocks.ELECTRIC_NODE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OldNodeBlock.OldNodeBlockEntity>> TRANS_CATENARY_NODE =
			register("trans_catenary_node", () -> BlockEntityType.Builder.of(
					(pos, state) -> new OldNodeBlock.OldNodeBlockEntity(getTransCatenaryNodeType(), pos, state),
					MSDBlocks.TRANS_CATENARY_NODE.get()).build(null));

	private MSDBlockEntities() {
	}

	public static void register(IEventBus modEventBus) {
		BLOCK_ENTITY_TYPES.register(modEventBus);
	}

	private static BlockEntityType<YuuniPIDSBlock.YuuniPIDSBlockEntity> getYuuniPidsType() {
		return YUUNI_PIDS.get();
	}

	private static BlockEntityType<YuuniPIDSBlock.YuuniPIDSBlockEntity> getYuuni2PidsType() {
		return YUUNI_2_PIDS.get();
	}

	private static BlockEntityType<Yamanote4PIDSBlock.Yamanote4PIDSBlockEntity> getYamanote4PidsType() {
		return YAMANOTE_4_PIDS.get();
	}

	private static BlockEntityType<RigidCatenaryNodeBlock.RigidCatenaryNodeBlockEntity> getRigidCatenaryNodeType() {
		return RIGID_CATENARY_NODE.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign2EvenType() {
		return YAMANOTE_RAILWAY_SIGN_2_EVEN.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign2OddType() {
		return YAMANOTE_RAILWAY_SIGN_2_ODD.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign3EvenType() {
		return YAMANOTE_RAILWAY_SIGN_3_EVEN.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign3OddType() {
		return YAMANOTE_RAILWAY_SIGN_3_ODD.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign4EvenType() {
		return YAMANOTE_RAILWAY_SIGN_4_EVEN.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign4OddType() {
		return YAMANOTE_RAILWAY_SIGN_4_ODD.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign5EvenType() {
		return YAMANOTE_RAILWAY_SIGN_5_EVEN.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign5OddType() {
		return YAMANOTE_RAILWAY_SIGN_5_ODD.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign6EvenType() {
		return YAMANOTE_RAILWAY_SIGN_6_EVEN.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign6OddType() {
		return YAMANOTE_RAILWAY_SIGN_6_ODD.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign7EvenType() {
		return YAMANOTE_RAILWAY_SIGN_7_EVEN.get();
	}

	private static BlockEntityType<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> getYamanoteRailwaySign7OddType() {
		return YAMANOTE_RAILWAY_SIGN_7_ODD.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithLongType() {
		return CATENARY_WITH_LONG.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithLongTopType() {
		return CATENARY_WITH_LONG_TOP.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithShortType() {
		return CATENARY_WITH_SHORT.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithShortTopType() {
		return CATENARY_WITH_SHORT_TOP.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithLongCounterweightType() {
		return CATENARY_WITH_LONG_COUNTERWEIGHT.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithLongCounterweightMirrorType() {
		return CATENARY_WITH_LONG_COUNTERWEIGHT_MIRROR.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithShortCounterweightType() {
		return CATENARY_WITH_SHORT_COUNTERWEIGHT.get();
	}

	private static BlockEntityType<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> getCatenaryWithShortCounterweightMirrorType() {
		return CATENARY_WITH_SHORT_COUNTERWEIGHT_MIRROR.get();
	}

	private static BlockEntityType<AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity> getYamanote5PidsType() {
		return YAMANOTE_5_PIDS.get();
	}

	private static BlockEntityType<AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity> getYamanote6PidsType() {
		return YAMANOTE_6_PIDS.get();
	}

	private static BlockEntityType<AbstractYamanotePIDSBlock.YamanotePIDSBlockEntity> getYamanote7PidsType() {
		return YAMANOTE_7_PIDS.get();
	}

	private static BlockEntityType<AbstractStandingSignBlock.StandingSignBlockEntity> getYuuniStandingSignType() {
		return YUUNI_STANDING_SIGN.get();
	}

	private static BlockEntityType<AbstractStandingSignBlock.StandingSignBlockEntity> getYuuniStandingSign1Type() {
		return YUUNI_STANDING_SIGN_1.get();
	}

	private static BlockEntityType<OldNodeBlock.OldNodeBlockEntity> getCatenaryNodeType() {
		return CATENARY_NODE.get();
	}

	private static BlockEntityType<OldNodeBlock.OldNodeBlockEntity> getCatenaryNodeStyle2Type() {
		return CATENARY_NODE_STYLE_2.get();
	}

	private static BlockEntityType<OldNodeBlock.OldNodeBlockEntity> getShortCatenaryNodeType() {
		return SHORT_CATENARY_NODE.get();
	}

	private static BlockEntityType<OldNodeBlock.OldNodeBlockEntity> getShortCatenaryNodeStyle2Type() {
		return SHORT_CATENARY_NODE_STYLE_2.get();
	}

	private static BlockEntityType<OldNodeBlock.OldNodeBlockEntity> getElectricNodeType() {
		return ELECTRIC_NODE.get();
	}

	private static BlockEntityType<OldNodeBlock.OldNodeBlockEntity> getTransCatenaryNodeType() {
		return TRANS_CATENARY_NODE.get();
	}

	@SuppressWarnings("unchecked")
	private static <T extends net.minecraft.world.level.block.entity.BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
			String name, Supplier<BlockEntityType<T>> supplier) {
		return (DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>) (DeferredHolder<?, ?>) BLOCK_ENTITY_TYPES.register(name, supplier);
	}
}
