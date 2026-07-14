package top.mcmtr.render;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.neoforged.fml.loading.FMLPaths;

public final class StandingSignRenderCalibration {

	private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("msd/standing-sign-render-calibration.properties");
	private static final StandingSignRenderCalibration INSTANCE = load();
	private final Map<String, Transform> transforms = new HashMap<>();

	private StandingSignRenderCalibration() {
	}

	public static Transform get(String signType) {
		if (!RenderCalibrationFeature.isEnabled()) {
			return Transform.ZERO;
		}
		return INSTANCE.transforms.getOrDefault(signType, Transform.ZERO);
	}

	public static void set(String signType, Transform transform) {
		if (!RenderCalibrationFeature.isEnabled()) {
			return;
		}
		if (transform.equals(Transform.ZERO)) {
			INSTANCE.transforms.remove(signType);
		} else {
			INSTANCE.transforms.put(signType, transform);
		}
	}

	public static void save() {
		if (!RenderCalibrationFeature.isEnabled()) {
			return;
		}
		Properties properties = new Properties();
		INSTANCE.transforms.forEach((signType, transform) -> {
			properties.setProperty(signType + ".yaw", Float.toString(transform.yaw()));
			properties.setProperty(signType + ".x", Float.toString(transform.x()));
			properties.setProperty(signType + ".y", Float.toString(transform.y()));
			properties.setProperty(signType + ".z", Float.toString(transform.z()));
			properties.setProperty(signType + ".front_depth", Float.toString(transform.frontDepth()));
			properties.setProperty(signType + ".back_depth", Float.toString(transform.backDepth()));
		});
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (var output = Files.newOutputStream(CONFIG_PATH)) {
				properties.store(output, "MSD standing sign renderer calibration");
			}
		} catch (IOException exception) {
			throw new UncheckedIOException("Failed to save standing sign render calibration", exception);
		}
	}

	public static StandingSignRenderTuning resolve(String signType) {
		StandingSignRenderTuning base = StandingSignRenderTuning.active();
		Transform delta = get(signType);
		return new StandingSignRenderTuning(
				base.yawCorrectionDegrees() + delta.yaw(),
				base.translateX() + delta.x(),
				base.translateY() + delta.y(),
				base.translateZ() + delta.z(),
				base.frontDepthNudge() + delta.frontDepth(),
				base.backDepthNudge() + delta.backDepth(),
				base.renderBackText(),
				base.seeThroughText());
	}

	private static StandingSignRenderCalibration load() {
		StandingSignRenderCalibration calibration = new StandingSignRenderCalibration();
		if (!RenderCalibrationFeature.isEnabled() || !Files.exists(CONFIG_PATH)) {
			return calibration;
		}
		Properties properties = new Properties();
		try (var input = Files.newInputStream(CONFIG_PATH)) {
			properties.load(input);
		} catch (IOException exception) {
			throw new UncheckedIOException("Failed to read standing sign render calibration", exception);
		}
		for (String key : properties.stringPropertyNames()) {
			if (!key.endsWith(".yaw")) {
				continue;
			}
			String signType = key.substring(0, key.length() - 4);
			calibration.transforms.put(signType, new Transform(
					readFloat(properties, signType + ".yaw"),
					readFloat(properties, signType + ".x"),
					readFloat(properties, signType + ".y"),
					readFloat(properties, signType + ".z"),
					readFloat(properties, signType + ".front_depth"),
					readFloat(properties, signType + ".back_depth")));
		}
		return calibration;
	}

	private static float readFloat(Properties properties, String key) {
		try {
			return Float.parseFloat(properties.getProperty(key, "0"));
		} catch (NumberFormatException ignored) {
			return 0F;
		}
	}

	public record Transform(float yaw, float x, float y, float z, float frontDepth, float backDepth) {
		public static final Transform ZERO = new Transform(0F, 0F, 0F, 0F, 0F, 0F);
	}
}
