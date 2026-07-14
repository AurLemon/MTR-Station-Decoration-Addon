package top.mcmtr.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.screen.PIDSConfigScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mcmtr.render.RenderCalibrationFeature;
import top.mcmtr.render.RenderCalibrationKey;
import top.mcmtr.screen.PidsRenderCalibrationScreen;
import top.mcmtr.util.ScreenWidgetHelper;

@Mixin(PIDSConfigScreen.class)
public abstract class PIDSConfigScreenMixin {

	@Shadow(remap = false)
	@Final
	private BlockPos blockPos;

	@Unique
	private Button msd$renderCalibrationButton;

	@Inject(method = "onTick", at = @At("TAIL"), remap = false)
	private void msd$addRenderCalibrationButton(CallbackInfo ci) {
		if (!RenderCalibrationFeature.isEnabled() || msd$renderCalibrationButton != null) {
			return;
		}
		Screen screen = (Screen) (Object) this;
		msd$renderCalibrationButton = ScreenWidgetHelper.addButton(screen, Button.builder(Component.literal("Render Calibration"), button -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.level == null) {
				return;
			}
			BlockEntity blockEntity = minecraft.level.getBlockEntity(blockPos);
			if (blockEntity == null) {
				return;
			}
			minecraft.setScreen(new PidsRenderCalibrationScreen(RenderCalibrationKey.fromBlockState(blockEntity.getBlockState()), screen));
		}).bounds(screen.width - 170, screen.height - 28, 160, 20).build());
	}
}
