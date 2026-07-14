package top.mcmtr.render;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.neoforged.fml.loading.FMLPaths;

public final class YamanoteRenderCalibration {

	private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("msd/yamanote-render-calibration.properties");
	private static final YamanoteRenderCalibration INSTANCE = load();
	private final Map<String, Transform> transforms = new HashMap<>();

	private YamanoteRenderCalibration() {
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
			properties.setProperty(signType + ".x", Float.toString(transform.x()));
			properties.setProperty(signType + ".y", Float.toString(transform.y()));
			properties.setProperty(signType + ".z", Float.toString(transform.z()));
			properties.setProperty(signType + ".yaw", Float.toString(transform.yaw()));
		});
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (var output = Files.newOutputStream(CONFIG_PATH)) {
				properties.store(output, "MSD Yamanote renderer calibration");
			}
		} catch (IOException exception) {
			throw new UncheckedIOException("Failed to save Yamanote render calibration", exception);
		}
	}

	private static YamanoteRenderCalibration load() {
		YamanoteRenderCalibration calibration = new YamanoteRenderCalibration();
		if (!RenderCalibrationFeature.isEnabled() || !Files.exists(CONFIG_PATH)) {
			return calibration;
		}
		Properties properties = new Properties();
		try (var input = Files.newInputStream(CONFIG_PATH)) {
			properties.load(input);
		} catch (IOException exception) {
			throw new UncheckedIOException("Failed to read Yamanote render calibration", exception);
		}
		for (String key : properties.stringPropertyNames()) {
			if (!key.endsWith(".x")) {
				continue;
			}
			String signType = key.substring(0, key.length() - 2);
			calibration.transforms.put(signType, new Transform(
					readFloat(properties, signType + ".x"),
					readFloat(properties, signType + ".y"),
					readFloat(properties, signType + ".z"),
					readFloat(properties, signType + ".yaw")));
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

	public record Transform(float x, float y, float z, float yaw) {
		public static final Transform ZERO = new Transform(0F, 0F, 0F, 0F);
	}
}
