package top.mcmtr.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.screen.RailwaySignScreen;
import org.mtr.screen.WindowBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mcmtr.render.RenderCalibrationFeature;
import top.mcmtr.render.RenderCalibrationKey;
import top.mcmtr.screen.YamanoteRenderCalibrationScreen;
import top.mcmtr.util.ScreenWidgetHelper;

@Mixin(WindowBase.class)
public abstract class WindowBaseMixin {

	@Unique
	private Button msd$renderCalibrationButton;

	@Inject(method = "onTick", at = @At("TAIL"), remap = false)
	private void msd$addYamanoteCalibrationButton(CallbackInfo ci) {
		if (!RenderCalibrationFeature.isEnabled() || msd$renderCalibrationButton != null || !((Object) this instanceof RailwaySignScreen railwaySignScreen)) {
			return;
		}
		Screen screen = (Screen) (Object) this;
		msd$renderCalibrationButton = ScreenWidgetHelper.addButton(screen, Button.builder(Component.literal("Render Calibration"), button -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.level == null) {
				return;
			}
			BlockEntity blockEntity = minecraft.level.getBlockEntity(((RailwaySignScreenAccessor) (Object) railwaySignScreen).msd$getSignPos());
			if (blockEntity == null) {
				return;
			}
			minecraft.setScreen(new YamanoteRenderCalibrationScreen(RenderCalibrationKey.fromBlockState(blockEntity.getBlockState()), screen));
		}).bounds(screen.width - 170, screen.height - 28, 160, 20).build());
	}

	@Inject(method = "onScreenClose", at = @At("HEAD"), remap = false, require = 0)
	private void msd$cleanupCalibrationButton(CallbackInfo ci) {
		msd$renderCalibrationButton = null;
	}
}
