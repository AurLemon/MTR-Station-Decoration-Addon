package top.mcmtr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import org.mtr.block.IBlock;
import org.mtr.render.BlockEntityRendererExtension;
import top.mcmtr.block.AbstractStandingSignBlock;

public class RenderCustomText extends BlockEntityRendererExtension<AbstractStandingSignBlock.StandingSignBlockEntity> {

	private final int maxMessages;
	private final float startX;
	private final float startY;
	private final float startZ;
	private final float maxHeight;
	private final float maxWidth;
	private final boolean rotate90;
	private final float scale;
	private final float firstRowScale;
	private final float secondRowScale;
	private final float rowSpacing;
	private final int[] colors;

	public RenderCustomText(
			int maxMessages,
			float startX,
			float startY,
			float startZ,
			float maxHeight,
			float maxWidth,
			boolean rotate90,
			float textPadding,
			float firstRowTextPadding,
			float secondRowTextPadding,
			float rowSpacing,
			int... colors) {
		this.maxMessages = maxMessages;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
		this.rotate90 = rotate90;
		this.scale = 160 * maxMessages / maxHeight * textPadding;
		this.firstRowScale = 160 * maxMessages / maxHeight * firstRowTextPadding;
		this.secondRowScale = 160 * maxMessages / maxHeight * secondRowTextPadding;
		this.rowSpacing = rowSpacing;
		this.colors = new int[maxMessages];
		System.arraycopy(colors, 0, this.colors, 0, Math.min(colors.length, maxMessages));
		if (colors.length < maxMessages) {
			for (int i = colors.length; i < maxMessages; i++) {
				this.colors[i] = colors[colors.length - 1];
			}
		}
	}

	@Override
	public void render(
			AbstractStandingSignBlock.StandingSignBlockEntity blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			ClientLevel level,
			LocalPlayer player,
			float partialTick,
			int packedLight,
			int packedOverlay) {
		final Direction facing = IBlock.getStatePropertySafe(blockEntity.getBlockState(), AbstractStandingSignBlock.FACING);
		renderSide(blockEntity, poseStack, bufferSource, packedLight, facing, false);
		renderSide(blockEntity, poseStack, bufferSource, packedLight, facing.getOpposite(), true);
	}

	private void renderSide(
			AbstractStandingSignBlock.StandingSignBlockEntity blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			Direction facing,
			boolean rightAlign) {
		for (int i = 0; i < maxMessages; i++) {
			final String[] splitText = blockEntity.getMessage(i).split("\\|", 2);
			final String bigText = splitText[0];
			final String smallText = splitText.length > 1 ? splitText[1] : null;
			final float bigScale = smallText == null ? scale : firstRowScale;
			final float smallScale = smallText == null ? 0 : secondRowScale;

			poseStack.pushPose();
			poseStack.translate(0.5D, 0D, 0.5D);
			poseStack.mulPose(Axis.YP.rotationDegrees((rotate90 ? 90 : 0) - facing.toYRot()));
			poseStack.mulPose(Axis.ZP.rotationDegrees(180));
			poseStack.translate(rightAlign ? (13 - startX) / 16F : (startX - 8) / 16F, -startY / 16F + i * maxHeight / maxMessages / 16F, (startZ - 8) / 16F - 0.0025F);
			renderText(poseStack, bufferSource, bigText, colors[i], maxWidth * bigScale / 16F, 0, bigScale, rightAlign, packedLight);
			if (smallText != null) {
				renderText(poseStack, bufferSource, smallText, colors[i], maxWidth * smallScale / 16F, 8 / bigScale + rowSpacing, smallScale, rightAlign, packedLight);
			}
			poseStack.popPose();
		}
	}

	private static void renderText(
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			String text,
			int color,
			float maxWidth,
			double y,
			float scale,
			boolean rightAlign,
			int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0, y, 0);
		poseStack.scale(1 / scale, 1 / scale, 1 / scale);
		final net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
		final int textWidth = font.width(text);
		if (textWidth > maxWidth) {
			poseStack.scale(maxWidth / textWidth, 1, 1);
		}
		font.drawInBatch(text, rightAlign ? Math.max(0, maxWidth - textWidth) : 0, 0, color | 0xFF000000, false, poseStack.last().pose(), bufferSource, net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, packedLight);
		poseStack.popPose();
	}
}
