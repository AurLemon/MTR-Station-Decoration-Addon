package top.mcmtr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import top.mcmtr.blocks.BlockCustomTextSignBase;
import top.mcmtr.config.Config;

public class RenderCustomTextSign<T extends BlockEntityMapper> extends BlockEntityRendererMapper<T> implements IGui, IDrawing {
    private final float scale;
    private final float fRowScale;
    private final float sRowScale;
    private final float fRowTotalScaledWidth;
    private final float sRowTotalScaledWidth;
    private final float rowSpacing;
    private final float totalScaledWidth;
    private final int maxArrivals;
    private final float maxHeight;
    private final float startX;
    private final float startY;
    private final float startZ;
    private final boolean rotate90;
    private final int textColor;
    private final boolean enableFirstTextColor;
    private final int firstTextColor;

    /**
     * (总行高x0.8)/ (行数x缩放倍数)= 文字高度
     */
    public RenderCustomTextSign(BlockEntityRenderDispatcher dispatcher, int maxArrivals, float startX, float startY, float startZ, float maxHeight, int maxWidth, boolean rotate90, int textColor, boolean enableFirstTextColor, int firstTextColor, float textPadding, float fRowTextPadding, float sRowTextPadding, float rowSpacing) {
        super(dispatcher);
        this.scale = 160 * maxArrivals / maxHeight * textPadding;
        this.totalScaledWidth = scale * maxWidth / 16;
        this.fRowScale = 160 * maxArrivals / maxHeight * fRowTextPadding;
        this.fRowTotalScaledWidth = fRowScale * maxWidth / 16;
        this.sRowScale = 160 * maxArrivals / maxHeight * sRowTextPadding;
        this.sRowTotalScaledWidth = sRowScale * maxWidth / 16;
        this.maxArrivals = maxArrivals;
        this.maxHeight = maxHeight;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.rotate90 = rotate90;
        this.textColor = textColor;
        this.enableFirstTextColor = enableFirstTextColor;
        this.firstTextColor = firstTextColor;
        this.rowSpacing = rowSpacing;
    }

    @Environment(EnvType.CLIENT)
    public int getViewDistance() {
        return Config.getCustomTextSignMaxViewDistance();
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final BlockGetter world = entity.getLevel();
        if (world == null) {
            return;
        }
        final BlockPos pos = entity.getBlockPos();
        final Direction facing = IBlock.getStatePropertySafe(world, pos, HorizontalDirectionalBlock.FACING);
        if (RenderTrains.shouldNotRender(pos, RenderTrains.maxTrainRenderDistance, rotate90 ? null : facing)) {
            return;
        }

        final String[] customMessages = new String[maxArrivals];
        for (int i = 0; i < maxArrivals; i++) {
            if (entity instanceof BlockCustomTextSignBase.TileEntityBlockCustomTextSignBase) {
                customMessages[i] = ((BlockCustomTextSignBase.TileEntityBlockCustomTextSignBase) entity).getMessage(i);
            } else {
                customMessages[i] = "";
            }
        }

        try {
            final Font textRenderer = Minecraft.getInstance().font;
            
            for (int side = 0; side < 2; side++) {
                final boolean isBackSide = side == 1;

                for (int i = 0; i < maxArrivals; i++) {
                    final String destinationString;
                    final String destinationString2;
                    final float trueFRowScale;
                    final float trueFRowTotalScaledWidth;
                    final int trueColor;
                    final String[] destinationSplit = customMessages[i].split("\\|");
                    destinationString = destinationSplit[0];
                    if (destinationSplit.length > 1) {
                        destinationString2 = destinationSplit[1];
                        trueFRowScale = fRowScale;
                        trueFRowTotalScaledWidth = fRowTotalScaledWidth;
                    } else {
                        destinationString2 = "";
                        trueFRowScale = scale;
                        trueFRowTotalScaledWidth = totalScaledWidth;
                    }
                    if (enableFirstTextColor && (i == 0)) {
                        trueColor = firstTextColor;
                    } else {
                        trueColor = textColor;
                    }
                    
                    matrices.pushPose();
                    matrices.translate(0.5, 0, 0.5);
                    if (isBackSide) {
                        UtilitiesClient.rotateYDegrees(matrices, (rotate90 ? 90 : 0) - facing.toYRot() + 180);
                    } else {
                        UtilitiesClient.rotateYDegrees(matrices, (rotate90 ? 90 : 0) - facing.toYRot());
                    }
                    UtilitiesClient.rotateZDegrees(matrices, 180);
                    matrices.translate((startX - 8) / 16, -startY / 16 + i * maxHeight / maxArrivals / 16, (startZ - 8) / 16 - SMALL_OFFSET * 2);
                    matrices.scale(1F / trueFRowScale, 1F / trueFRowScale, 1F / trueFRowScale);
                    final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(com.mojang.blaze3d.vertex.Tesselator.getInstance().getBuilder());
                    IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource, destinationString, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 6, trueFRowTotalScaledWidth, 16, 1F / trueFRowScale, trueColor, false, light, null);
                    bufferSource.endBatch();
                    matrices.popPose();
                    
                    if (!destinationString2.isEmpty()) {
                        matrices.pushPose();
                        matrices.translate(0.5, 0, 0.5);
                        if (isBackSide) {
                            UtilitiesClient.rotateYDegrees(matrices, (rotate90 ? 90 : 0) - facing.toYRot() + 180);
                        } else {
                            UtilitiesClient.rotateYDegrees(matrices, (rotate90 ? 90 : 0) - facing.toYRot());
                        }
                        UtilitiesClient.rotateZDegrees(matrices, 180);
                        matrices.translate((startX - 8) / 16, (-startY / 16 + i * maxHeight / maxArrivals / 16) + (8 / fRowScale) + rowSpacing, (startZ - 8) / 16 - SMALL_OFFSET * 2);
                        matrices.scale(1F / sRowScale, 1F / sRowScale, 1F / sRowScale);
                        final MultiBufferSource.BufferSource bufferSource2 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                        IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource2, destinationString2, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 6, sRowTotalScaledWidth, 16, 1F / sRowScale, trueColor, false, light, null);
                        bufferSource2.endBatch();
                        matrices.popPose();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}