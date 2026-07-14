package top.mcmtr.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.mtr.block.BlockRailwaySign;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.screen.RailwaySignScreen;
import top.mcmtr.block.AbstractStandingSignBlock;
import top.mcmtr.block.BlockCatenaryWithModel;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.block.YamanoteRailwaySignBlock;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.screen.CatenaryScreen;
import top.mcmtr.screen.CatenaryWithModelScreen;
import top.mcmtr.screen.CustomTextScreen;
import top.mcmtr.screen.RigidCatenaryShapeModifierScreen;

public final class MSDClientPacketHelper {

	private MSDClientPacketHelper() {
	}

	public static void openYamanoteRailwaySignScreen(BlockPos blockPos) {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientLevel level = minecraft.level;
		if (level == null) {
			return;
		}

		final BlockEntity blockEntity = level.getBlockEntity(blockPos);
		final BlockState blockState = level.getBlockState(blockPos);
		if (!(blockEntity instanceof YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity signBlockEntity)
				|| !(blockState.getBlock() instanceof YamanoteRailwaySignBlock signBlock)) {
			return;
		}

		final BlockRailwaySign.RailwaySignBlockEntity screenAdapter = createYamanoteScreenAdapter(blockPos, signBlock, signBlockEntity);
		if (screenAdapter == null) {
			return;
		}

		minecraft.setScreen(new RailwaySignScreen(blockPos, screenAdapter));
	}

	private static BlockRailwaySign.RailwaySignBlockEntity createYamanoteScreenAdapter(
			BlockPos blockPos,
			YamanoteRailwaySignBlock signBlock,
			YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity signBlockEntity) {
		final String blockId = "railway_sign_" + signBlock.getLength() + "_" + (signBlock.isOdd() ? "odd" : "even");
		final Block block = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath("mtr", blockId)).orElse(null);
		if (block == null) {
			return null;
		}

		final BlockRailwaySign.RailwaySignBlockEntity adapter = new BlockRailwaySign.RailwaySignBlockEntity(
				signBlock.getLength(),
				signBlock.isOdd(),
				blockPos,
				block.defaultBlockState());
		final LongAVLTreeSet[] selectedIds = signBlockEntity.getSelectedIds();
		final LongAVLTreeSet[] clonedSelectedIds = new LongAVLTreeSet[selectedIds.length];
		for (int i = 0; i < selectedIds.length; i++) {
			clonedSelectedIds[i] = new LongAVLTreeSet(selectedIds[i]);
		}
		adapter.setData(clonedSelectedIds, signBlockEntity.getSignIds().clone());
		return adapter;
	}

	public static void openCustomTextScreen(BlockPos blockPos, int maxArrivals) {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientLevel level = minecraft.level;
		if (level == null) {
			return;
		}

		final BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (!(blockEntity instanceof AbstractStandingSignBlock.StandingSignBlockEntity standingSignBlockEntity)) {
			return;
		}

		minecraft.setScreen(new CustomTextScreen(blockPos, maxArrivals, standingSignBlockEntity));
	}

	public static void openCatenaryScreen(boolean isConnected, BlockPos blockPos) {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientLevel level = minecraft.level;
		if (level == null) {
			return;
		}

		final BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (!(blockEntity instanceof OldNodeBlock.OldNodeBlockEntity oldNodeBlockEntity)) {
			return;
		}

		minecraft.setScreen(new CatenaryScreen(blockPos, isConnected, oldNodeBlockEntity));
	}

	public static void openCatenaryWithModelScreen(BlockPos blockPos, boolean isConnected) {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientLevel level = minecraft.level;
		if (level == null) {
			return;
		}

		final BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (!(blockEntity instanceof BlockCatenaryWithModel.BlockCatenaryWithModelEntity catenaryWithModelEntity)) {
			return;
		}

		minecraft.setScreen(new CatenaryWithModelScreen(blockPos, isConnected, catenaryWithModelEntity));
	}

	public static void openRigidCatenaryShapeScreen(RigidCatenary rigidCatenary) {
		Minecraft.getInstance().setScreen(new RigidCatenaryShapeModifierScreen(rigidCatenary));
	}
}
