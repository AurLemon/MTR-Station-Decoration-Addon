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
		final int length = signBlock.getLength();
		final org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet[] selectedIds = new org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet[length];
		for (int i = 0; i < length; i++) {
			selectedIds[i] = blockEntity.getSelectedIds();
		}

		poseStack.pushPose();
		poseStack.translate(signBlock.getXStart() / 16F - 0.5F, 0.03125F, -0.0625F);
		poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		poseStack.mulPose(Axis.ZP.rotationDegrees(180));
		SignResource.render(
				poseStack,
				bufferSource,
				blockEntity.getBlockPos(),
				selectedIds,
				blockEntity.getSignIds(),
				0.5F,
				0.0F,
				false);
		poseStack.popPose();
	}
}
