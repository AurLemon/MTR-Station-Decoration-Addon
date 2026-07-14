package top.mcmtr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import org.mtr.block.IBlock;
import org.mtr.client.CustomResourceLoader;
import org.mtr.render.BlockEntityRendererExtension;
import org.mtr.render.StoredMatrixTransformations;
import top.mcmtr.block.BlockCatenaryWithModel;

public class RenderCatenaryModel extends BlockEntityRendererExtension<BlockCatenaryWithModel.BlockCatenaryWithModelEntity> {

	@Override
	public void render(
			BlockCatenaryWithModel.BlockCatenaryWithModelEntity blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			ClientLevel level,
			LocalPlayer player,
			float partialTick,
			int packedLight,
			int packedOverlay) {
		final Direction facing = IBlock.getStatePropertySafe(blockEntity.getBlockState(), BlockCatenaryWithModel.FACING);
		// Anchor the model in world space so camera movement does not drag it around.
		final double x = 0.5 + blockEntity.getBlockPos().getX();
		final double y = blockEntity.getBlockPos().getY();
		final double z = 0.5 + blockEntity.getBlockPos().getZ();
		final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(
				x,
				y,
				z);
		storedMatrixTransformations.add(graphicsHolder -> {
			graphicsHolder.translate(blockEntity.getOffsetX(), blockEntity.getOffsetY(), blockEntity.getOffsetZ());
			graphicsHolder.mulPose(Axis.YP.rotationDegrees(180 - facing.toYRot()));
			graphicsHolder.mulPose(Axis.XP.rotationDegrees((float) blockEntity.getRotationX() + 180));
			graphicsHolder.mulPose(Axis.YP.rotationDegrees((float) blockEntity.getRotationY()));
			graphicsHolder.mulPose(Axis.ZP.rotationDegrees((float) blockEntity.getRotationZ()));
		});
		CustomResourceLoader.getObjectById(blockEntity.getCatenaryModel().getModelId(), objectResource ->
				objectResource.render(storedMatrixTransformations, packedLight));
	}
}
