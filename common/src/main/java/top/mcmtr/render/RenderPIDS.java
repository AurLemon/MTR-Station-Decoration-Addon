package top.mcmtr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import mtr.MTRClient;
import mtr.block.BlockArrivalProjectorBase;
import mtr.block.BlockPIDSBaseHorizontal;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.*;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.Text;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import top.mcmtr.config.Config;

import java.util.*;

public class RenderPIDS<T extends BlockEntityMapper> extends BlockEntityRendererMapper<T> implements IGui, IDrawing {
    private final float scale;
    private final float totalScaledWidth;
    private final float destinationStart;
    private final float destinationMaxWidth;
    private final float platformMaxWidth;
    private final float arrivalMaxWidth;
    private final int maxArrivals;
    private final float maxHeight;
    private final float startX;
    private final float startY;
    private final float startZ;
    private final boolean rotate90;
    private final boolean renderArrivalNumber;
    private final PIDSType renderType;
    private final int textColor;
    private final int firstTrainColor;
    private final boolean appendDotAfterMin;

    private static final int SWITCH_LANGUAGE_TICKS = 60;
    private static final int CAR_TEXT_COLOR = 0xFF0000;

    public RenderPIDS(BlockEntityRenderDispatcher dispatcher, int maxArrivals, float startX, float startY, float startZ, float maxHeight, int maxWidth, boolean rotate90, boolean renderArrivalNumber, PIDSType renderType, int textColor, int firstTrainColor, float textPadding, boolean appendDotAfterMin) {
        super(dispatcher);
        scale = 160 * maxArrivals / maxHeight * textPadding;
        totalScaledWidth = scale * maxWidth / 16;
        destinationStart = renderArrivalNumber ? scale * 2 / 16 : 0;
        destinationMaxWidth = totalScaledWidth * 0.7F;
        platformMaxWidth = renderType.showPlatformNumber ? scale * 2 / 16 : 0;
        arrivalMaxWidth = totalScaledWidth - destinationStart - destinationMaxWidth - platformMaxWidth;
        this.maxArrivals = maxArrivals;
        this.maxHeight = maxHeight;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.rotate90 = rotate90;
        this.renderArrivalNumber = renderArrivalNumber;
        this.renderType = renderType;
        this.textColor = textColor;
        this.firstTrainColor = firstTrainColor;
        this.appendDotAfterMin = appendDotAfterMin;
    }

    @Environment(EnvType.CLIENT)
    public int getViewDistance() {
        return Config.getYuuniPIDSMaxViewDistance();
    }


    public RenderPIDS(BlockEntityRenderDispatcher dispatcher, int maxArrivals, float startX, float startY, float startZ, float maxHeight, int maxWidth, boolean rotate90, boolean renderArrivalNumber, PIDSType renderType, int textColor, int firstTrainColor) {
        this(dispatcher, maxArrivals, startX, startY, startZ, maxHeight, maxWidth, rotate90, renderArrivalNumber, renderType, textColor, firstTrainColor, 1, false);
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
        final boolean[] hideArrival = new boolean[maxArrivals];
        for (int i = 0; i < maxArrivals; i++) {
            if (entity instanceof BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal) {
                customMessages[i] = ((BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal) entity).getMessage(i);
                hideArrival[i] = ((BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal) entity).getHideArrival(i);
            } else {
                customMessages[i] = "";
            }
        }
        try {
            final Map<Long, String> platformIdToName = new HashMap<>();
            final List<ScheduleEntry> scheduleList = getSchedules(entity, pos, platformIdToName);
            final boolean showCarLength;
            final float carLengthMaxWidth;
            if (renderType.showCarCount) {
                int maxCars = 0;
                int minCars = Integer.MAX_VALUE;
                for (final ScheduleEntry scheduleEntry : scheduleList) {
                    final int trainCars = scheduleEntry.trainCars;
                    if (trainCars > maxCars) {
                        maxCars = trainCars;
                    }
                    if (trainCars < minCars) {
                        minCars = trainCars;
                    }
                }
                showCarLength = minCars != maxCars;
                carLengthMaxWidth = showCarLength ? scale * 6 / 16 : 0;
            } else {
                showCarLength = false;
                carLengthMaxWidth = 0;
            }
            final int displayPageOffset = entity instanceof BlockArrivalProjectorBase.TileEntityArrivalProjectorBase ? ((BlockArrivalProjectorBase.TileEntityArrivalProjectorBase) entity).getDisplayPage() * maxArrivals : 0;
            for (int i = 0; i < maxArrivals; i++) {
                final int languageTicks = (int) Math.floor(MTRClient.getGameTick()) / SWITCH_LANGUAGE_TICKS;
                final String destinationString;
                final boolean useCustomMessage;
                final ScheduleEntry currentSchedule = i + displayPageOffset < scheduleList.size() ? scheduleList.get(i + displayPageOffset) : null;
                final Route route = currentSchedule == null ? null : ClientData.DATA_CACHE.routeIdMap.get(currentSchedule.routeId);
                if (i < scheduleList.size() && !hideArrival[i] && route != null) {
                    String routeDestination = getRouteDestination(route, currentSchedule);
                    final String[] destinationSplit = routeDestination.split("\\|");
                    final boolean isLightRailRoute = route.isLightRailRoute;
                    final String[] routeNumberSplit = route.lightRailRouteNumber.split("\\|");
                    if (customMessages[i].isEmpty()) {
                        destinationString = (isLightRailRoute ? routeNumberSplit[languageTicks % routeNumberSplit.length] + " " : "") + IGui.textOrUntitled(destinationSplit[languageTicks % destinationSplit.length]);
                        useCustomMessage = false;
                    } else {
                        final String[] customMessageSplit = customMessages[i].split("\\|");
                        final int destinationMaxIndex = Math.max(routeNumberSplit.length, destinationSplit.length);
                        final int indexToUse = languageTicks % (destinationMaxIndex + customMessageSplit.length);
                        if (indexToUse < destinationMaxIndex) {
                            destinationString = (isLightRailRoute ? routeNumberSplit[languageTicks % routeNumberSplit.length] + " " : "") + IGui.textOrUntitled(destinationSplit[languageTicks % destinationSplit.length]);
                            useCustomMessage = false;
                        } else {
                            destinationString = customMessageSplit[indexToUse - destinationMaxIndex];
                            useCustomMessage = true;
                        }
                    }
                } else {
                    final String[] destinationSplit = customMessages[i].split("\\|");
                    destinationString = destinationSplit[languageTicks % destinationSplit.length];
                    useCustomMessage = true;
                }
                matrices.pushPose();
                matrices.translate(0.5, 0, 0.5);
                UtilitiesClient.rotateYDegrees(matrices, (rotate90 ? 90 : 0) - facing.toYRot());
                UtilitiesClient.rotateZDegrees(matrices, 180);
                matrices.translate((startX - 8) / 16, -startY / 16 + i * maxHeight / maxArrivals / 16, (startZ - 8) / 16 - SMALL_OFFSET * 2);
                matrices.scale(1F / scale, 1F / scale, 1F / scale);
                final Font textRenderer = Minecraft.getInstance().font;
                if (useCustomMessage) {
                    final int destinationWidth = textRenderer.width(destinationString);
                    if (destinationWidth > totalScaledWidth) {
                        matrices.scale(totalScaledWidth / destinationWidth, 1, 1);
                    }
                    final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource, destinationString, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 8, totalScaledWidth, 16, 1F / scale, textColor, false, light, null);
                    bufferSource.endBatch();
                } else {
                    final Component arrivalText;
                    final int seconds = (int) ((currentSchedule.arrivalMillis - System.currentTimeMillis()) / 1000);
                    final boolean isCJK = IGui.isCjk(destinationString);
                    if (seconds >= 60) {
                        arrivalText = Text.translatable(isCJK ? "gui.mtr.arrival_min_cjk" : "gui.mtr.arrival_min", seconds / 60).append(appendDotAfterMin && !isCJK ? "." : "");
                    } else {
                        arrivalText = seconds > 0 ? Text.translatable(isCJK ? "gui.mtr.arrival_sec_cjk" : "gui.mtr.arrival_sec", seconds).append(appendDotAfterMin && !isCJK ? "." : "") : null;
                    }
                    final Component carText = Text.translatable(isCJK ? "gui.mtr.arrival_car_cjk" : "gui.mtr.arrival_car", currentSchedule.trainCars);
                    if (renderArrivalNumber) {
                        final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource, String.valueOf(i + 1), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 8, destinationStart, 16, 1F / scale, seconds > 0 ? textColor : firstTrainColor, false, light, null);
                    bufferSource.endBatch();
                    }
                    final float newDestinationMaxWidth = destinationMaxWidth - carLengthMaxWidth;
                    if (renderType.showPlatformNumber) {
                        final String platformName = platformIdToName.get(route.platformIds.get(currentSchedule.currentStationIndex).platformId);
                        if (platformName != null) {
                            final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                            IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource, platformName, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, destinationStart + newDestinationMaxWidth, 8, platformMaxWidth, 16, 1F / scale, seconds > 0 ? textColor : firstTrainColor, false, light, null);
                            bufferSource.endBatch();
                        }
                    }
                    if (showCarLength) {
                        matrices.pushPose();
                        matrices.translate(destinationStart + newDestinationMaxWidth + platformMaxWidth, 0, 0);
                        final int carTextWidth = textRenderer.width(carText);
                        if (carTextWidth > carLengthMaxWidth) {
                            matrices.scale(carLengthMaxWidth / carTextWidth, 1, 1);
                        }
                        final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                        IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource, carText.getString(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 8, carLengthMaxWidth, 16, 1F / scale, CAR_TEXT_COLOR, false, light, null);
                        bufferSource.endBatch();
                        matrices.popPose();
                    }
                    matrices.pushPose();
                    matrices.translate(destinationStart, 0, 0);
                    final String destinationString2;
                    final Component terminalStation = Text.translatable("gui.msd.pid_terminal");
                    if (currentSchedule.currentStationIndex == route.platformIds.size() - 1) {
                        if (isCJK) {
                            destinationString2 = terminalStation.getString();
                        } else {
                            destinationString2 = "Terminating Here    ";
                        }
                    } else {
                        destinationString2 = destinationString;
                    }
                    final int destinationWidth = textRenderer.width(destinationString2);
                    if (destinationWidth > newDestinationMaxWidth) {
                        matrices.scale(newDestinationMaxWidth / destinationWidth, 1, 1);
                    }
                    final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    IDrawing.drawStringWithFont(matrices, textRenderer, bufferSource, destinationString2, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 8, newDestinationMaxWidth, 16, 1F / scale, seconds > 0 ? textColor : firstTrainColor, false, light, null);
                    bufferSource.endBatch();
                    matrices.popPose();
                    if (arrivalText != null) {
                        matrices.pushPose();
                        final int arrivalWidth = textRenderer.width(arrivalText);
                        final float arrivalStartX = destinationStart + newDestinationMaxWidth + platformMaxWidth + carLengthMaxWidth;
                        if (arrivalWidth > arrivalMaxWidth) {
                            matrices.translate(arrivalStartX, 0, 0);
                            matrices.scale(arrivalMaxWidth / arrivalWidth, 1, 1);
                        } else {
                            final float rightAlignedX = Math.min(arrivalStartX + arrivalMaxWidth - arrivalWidth, totalScaledWidth - arrivalWidth - 15);
                            matrices.translate(rightAlignedX, 0, 0);
                        }
                        final MultiBufferSource.BufferSource arrivalBufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                        IDrawing.drawStringWithFont(matrices, textRenderer, arrivalBufferSource, arrivalText.getString(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, 8, arrivalMaxWidth, 16, 1F / scale, textColor, false, light, null);
                        arrivalBufferSource.endBatch();
                        matrices.popPose();
                    }
                }
                matrices.popPose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ScheduleEntry> getSchedules(T entity, BlockPos pos, final Map<Long, String> platformIdToName) {
        final Station station = RailwayData.getStation(ClientData.STATIONS, ClientData.DATA_CACHE, pos);
        if (station == null) {
            return new ArrayList<>();
        }
        final Map<Long, Platform> platforms = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
        if (platforms.isEmpty()) {
            return new ArrayList<>();
        }
        final Set<Long> platformIds;
        switch (renderType) {
            case ARRIVAL_PROJECTOR:
                if (entity instanceof BlockArrivalProjectorBase.TileEntityArrivalProjectorBase) {
                    platformIds = ((BlockArrivalProjectorBase.TileEntityArrivalProjectorBase) entity).getPlatformIds();
                } else {
                    platformIds = new HashSet<>();
                }
                break;
            case PIDS:
                final Set<Long> tempPlatformIds;
                if (entity instanceof BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal) {
                    tempPlatformIds = ((BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal) entity).getPlatformIds();
                } else {
                    tempPlatformIds = new HashSet<>();
                }
                platformIds = tempPlatformIds.isEmpty() ? Collections.singleton(entity instanceof BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal ? ((BlockPIDSBaseHorizontal.TileEntityBlockPIDSBaseHorizontal) entity).getPlatformId(ClientData.PLATFORMS, ClientData.DATA_CACHE) : 0) : tempPlatformIds;
                break;
            default:
                platformIds = new HashSet<>();
        }
        final Set<ScheduleEntry> schedules = new HashSet<>();
        platforms.values().forEach(platform -> {
            if (platformIds.isEmpty() || platformIds.contains(platform.id)) {
                final Set<ScheduleEntry> scheduleForPlatform = ClientData.SCHEDULES_FOR_PLATFORM.get(platform.id);
                if (scheduleForPlatform != null) {
                    scheduleForPlatform.forEach(scheduleEntry -> {
                        final Route route = ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
                        if (route != null) {
                            schedules.add(scheduleEntry);
                            platformIdToName.put(platform.id, platform.name);
                        }
                    });
                }
            }
        });
        final List<ScheduleEntry> scheduleList = new ArrayList<>(schedules);
        Collections.sort(scheduleList);
        return scheduleList;
    }
    
    private String getRouteDestination(Route route, ScheduleEntry currentSchedule) {
        try {
            if (route.platformIds != null && !route.platformIds.isEmpty()) {
                Object lastPlatformObj = route.platformIds.get(route.platformIds.size() - 1);
                
                try {
                    java.lang.reflect.Field customDestinationField = lastPlatformObj.getClass().getField("customDestination");
                    String customDestination = (String) customDestinationField.get(lastPlatformObj);
                    
                    if (customDestination != null && !customDestination.trim().isEmpty()) {
                        return customDestination;
                    }
                } catch (Exception e) {
                    // 忽略错误，继续尝试其他方法
                }
                
                try {
                    java.lang.reflect.Field platformIdField = lastPlatformObj.getClass().getField("platformId");
                    long platformId = (Long) platformIdField.get(lastPlatformObj);
                    
                    Platform platform = ClientData.DATA_CACHE.platformIdMap.get(platformId);
                    if (platform != null) {
                        Station station = getStationFromPlatform(platform);
                        if (station != null && station.name != null && !station.name.isEmpty()) {
                            return station.name;
                        }
                    }
                } catch (Exception e) {
                    // 忽略错误，继续尝试其他方法
                }
            }
            
            return route.name;

        } catch (Exception e) {
            System.err.println("Error getting route destination: " + e.getMessage());
            return route.name;
        }
    }
    
    private Station getStationFromPlatform(Platform platform) {
        try {
            for (Station station : ClientData.STATIONS) {
                Map<Long, Platform> stationPlatforms = ClientData.DATA_CACHE.requestStationIdToPlatforms(station.id);
                if (stationPlatforms.containsKey(platform.id)) {
                    return station;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting station from platform: " + e.getMessage());
        }
        return null;
    }

}