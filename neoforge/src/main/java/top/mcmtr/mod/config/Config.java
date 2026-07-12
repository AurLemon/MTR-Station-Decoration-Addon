package top.mcmtr.mod.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config {

	private static final Logger LOGGER = LoggerFactory.getLogger("MTR Station Decoration Addon Config");
	private static final String SEGMENT_LENGTH = "rigid_catenary_segment_length";
	private static final String PIDS_MAX_VIEW_DISTANCE = "yuuni_pids_max_view_distance";
	private static final String RAILWAY_SIGN_MAX_VIEW_DISTANCE = "railway_sign_max_view_distance";
	private static final String TEXT_SIGN_MAX_VIEW_DISTANCE = "custom_text_sign_max_view_distance";
	private static final String ELECTRIC_CURVATURE_SCALE = "electric_curvature_scale";
	private static final int DEFAULT_RIGID_CATENARY_SEGMENT_LENGTH = 1;
	private static final int DEFAULT_YUUNI_PIDS_MAX_VIEW_DISTANCE = 128;
	private static final int DEFAULT_YAMANOTE_RAILWAY_SIGN_MAX_VIEW_DISTANCE = 128;
	private static final int DEFAULT_CUSTOM_TEXT_SIGN_MAX_VIEW_DISTANCE = 128;
	private static final int DEFAULT_ELECTRIC_CURVATURE_SCALE = 100;

	private static int rigidCatenarySegmentLength = DEFAULT_RIGID_CATENARY_SEGMENT_LENGTH;
	private static int yuuniPIDSMaxViewDistance = DEFAULT_YUUNI_PIDS_MAX_VIEW_DISTANCE;
	private static int yamanoteRailwaySignMaxViewDistance = DEFAULT_YAMANOTE_RAILWAY_SIGN_MAX_VIEW_DISTANCE;
	private static int customTextSignMaxViewDistance = DEFAULT_CUSTOM_TEXT_SIGN_MAX_VIEW_DISTANCE;
	private static int electricCurvatureScale = DEFAULT_ELECTRIC_CURVATURE_SCALE;

	private Config() {
	}

	public static int getRigidCatenarySegmentLength() {
		return rigidCatenarySegmentLength;
	}

	public static int getYuuniPIDSMaxViewDistance() {
		return yuuniPIDSMaxViewDistance;
	}

	public static int getYamanoteRailwaySignMaxViewDistance() {
		return yamanoteRailwaySignMaxViewDistance;
	}

	public static int getCustomTextSignMaxViewDistance() {
		return customTextSignMaxViewDistance;
	}

	public static int getElectricCurvatureScale() {
		return electricCurvatureScale;
	}

	public static void setRigidCatenarySegmentLength(int value) {
		rigidCatenarySegmentLength = clamp(value, 1, 4, DEFAULT_RIGID_CATENARY_SEGMENT_LENGTH);
		writeToFile();
	}

	public static void setYuuniPIDSMaxViewDistance(int value) {
		yuuniPIDSMaxViewDistance = value;
		writeToFile();
	}

	public static void setYamanoteRailwaySignMaxViewDistance(int value) {
		yamanoteRailwaySignMaxViewDistance = value;
		writeToFile();
	}

	public static void setCustomTextSignMaxViewDistance(int value) {
		customTextSignMaxViewDistance = value;
		writeToFile();
	}

	public static void setElectricCurvatureScale(int value) {
		electricCurvatureScale = value;
		writeToFile();
	}

	public static void refreshProperties() {
		final Path configPath = getConfigPath();
		if (configPath == null) {
			return;
		}

		try {
			final JsonObject jsonConfig = Utilities.parseJson(String.join("", Files.readAllLines(configPath))).getAsJsonObject();
			rigidCatenarySegmentLength = clamp(getInt(jsonConfig, SEGMENT_LENGTH, rigidCatenarySegmentLength), 1, 4, DEFAULT_RIGID_CATENARY_SEGMENT_LENGTH);
			yuuniPIDSMaxViewDistance = getInt(jsonConfig, PIDS_MAX_VIEW_DISTANCE, yuuniPIDSMaxViewDistance);
			yamanoteRailwaySignMaxViewDistance = getInt(jsonConfig, RAILWAY_SIGN_MAX_VIEW_DISTANCE, yamanoteRailwaySignMaxViewDistance);
			customTextSignMaxViewDistance = getInt(jsonConfig, TEXT_SIGN_MAX_VIEW_DISTANCE, customTextSignMaxViewDistance);
			electricCurvatureScale = getInt(jsonConfig, ELECTRIC_CURVATURE_SCALE, electricCurvatureScale);
		} catch (Exception e) {
			writeToFile();
		}
	}

	private static void writeToFile() {
		final Path configPath = getConfigPath();
		if (configPath == null) {
			return;
		}

		final JsonObject jsonConfig = new JsonObject();
		jsonConfig.addProperty(SEGMENT_LENGTH, rigidCatenarySegmentLength);
		jsonConfig.addProperty(PIDS_MAX_VIEW_DISTANCE, yuuniPIDSMaxViewDistance);
		jsonConfig.addProperty(RAILWAY_SIGN_MAX_VIEW_DISTANCE, yamanoteRailwaySignMaxViewDistance);
		jsonConfig.addProperty(TEXT_SIGN_MAX_VIEW_DISTANCE, customTextSignMaxViewDistance);
		jsonConfig.addProperty(ELECTRIC_CURVATURE_SCALE, electricCurvatureScale);

		try {
			if (!Files.exists(configPath.getParent())) {
				Files.createDirectories(configPath.getParent());
			}
			Files.write(configPath, Collections.singleton(Utilities.prettyPrint(jsonConfig)));
		} catch (IOException e) {
			LOGGER.error("Failed to write MSD config", e);
		}
	}

	private static Path getConfigPath() {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft == null) {
			return null;
		}
		return minecraft.gameDirectory.toPath().resolve("config").resolve("msd.json");
	}

	private static int getInt(JsonObject jsonObject, String key, int fallback) {
		try {
			return jsonObject.get(key).getAsInt();
		} catch (Exception ignored) {
			return fallback;
		}
	}

	private static int clamp(int value, int min, int max, int fallback) {
		if (value < min || value > max) {
			return fallback;
		}
		return value;
	}
}
