package top.mcmtr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import org.mtr.render.BlockEntityRendererExtension;
import org.mtr.resource.SignResource;
import top.mcmtr.block.YamanoteRailwaySignBlock;

public class RenderYamanoteRailwaySign extends BlockEntityRendererExtension<YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity> {

	@Override
	public void render(
			YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			ClientLevel level,
			LocalPlayer player,
			float partialTick,
			int packedLight,
			int packedOverlay) {
		if (!(blockEntity.getBlockState().getBlock() instanceof YamanoteRailwaySignBlock signBlock)) {
			return;
		}

		final Direction facing = blockEntity.getBlockState().getValue(YamanoteRailwaySignBlock.FACING);
		final YamanoteRenderCalibration.Transform calibration = YamanoteRenderCalibration.get(RenderCalibrationKey.fromBlockState(blockEntity.getBlockState()));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + calibration.yaw()));
		poseStack.mulPose(Axis.ZP.rotationDegrees(180));
		poseStack.translate(
				signBlock.getXStart() / 16F - 0.5F + calibration.x(),
				-0.53125F + calibration.y(),
				-0.06562500004656613F + calibration.z());
		SignResource.render(
				poseStack,
				bufferSource,
				blockEntity.getBlockPos(),
				blockEntity.getSelectedIds(),
				blockEntity.getSignIds(),
				0.5F,
				0.003125F,
				false);
		poseStack.popPose();
	}
}
