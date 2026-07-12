package top.mcmtr.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import top.mcmtr.block.AbstractStandingSignBlock;
import top.mcmtr.block.BlockCatenaryWithModel;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.block.YamanoteRailwaySignBlock;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.screen.CatenaryScreen;
import top.mcmtr.screen.CatenaryWithModelScreen;
import top.mcmtr.screen.CustomTextScreen;
import top.mcmtr.screen.RigidCatenaryShapeModifierScreen;
import top.mcmtr.screen.YamanoteRailwaySignScreen;

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

		minecraft.setScreen(new YamanoteRailwaySignScreen(blockPos, signBlock, signBlockEntity));
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
