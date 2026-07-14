package top.mcmtr.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.mtr.block.BlockPIDSBase;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.render.RenderPIDS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mcmtr.render.PidsRenderCalibration;
import top.mcmtr.render.RenderCalibrationFeature;
import top.mcmtr.render.RenderCalibrationKey;

@Mixin(RenderPIDS.class)
public abstract class RenderPIDSMixin {

	@Inject(
			method = "render(Lorg/mtr/block/BlockPIDSBase$BlockEntityBase;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;)V",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", shift = At.Shift.AFTER, ordinal = 0),
			remap = false)
	private void msd$applyPidsTranslation(
			BlockPIDSBase.BlockEntityBase blockEntity,
			BlockPos blockPos,
			Direction direction,
			ObjectArrayList<?> arrivals,
			PoseStack poseStack,
			Vec3 cameraPos,
			CallbackInfo ci) {
		if (!RenderCalibrationFeature.isEnabled()) {
			return;
		}
		PidsRenderCalibration.Transform transform = PidsRenderCalibration.get(RenderCalibrationKey.fromBlockState(blockEntity.getBlockState())).front();
		poseStack.translate(transform.x(), transform.y(), transform.z());
	}

	@Inject(
			method = "render(Lorg/mtr/block/BlockPIDSBase$BlockEntityBase;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;)V",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0),
			remap = false)
	private void msd$applyPidsTilt(
			BlockPIDSBase.BlockEntityBase blockEntity,
			BlockPos blockPos,
			Direction direction,
			ObjectArrayList<?> arrivals,
			PoseStack poseStack,
			Vec3 cameraPos,
			CallbackInfo ci) {
		if (!RenderCalibrationFeature.isEnabled()) {
			return;
		}
		float tilt = PidsRenderCalibration.get(RenderCalibrationKey.fromBlockState(blockEntity.getBlockState())).front().tilt();
		if (tilt != 0F) {
			poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
		}
	}
}
