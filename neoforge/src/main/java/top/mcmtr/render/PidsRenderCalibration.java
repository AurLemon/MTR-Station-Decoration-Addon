package top.mcmtr.render;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.neoforged.fml.loading.FMLPaths;

public final class PidsRenderCalibration {

	private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("msd/pids-render-calibration.properties");
	private static final PidsRenderCalibration INSTANCE = load();
	private final Map<String, Calibration> calibrations = new HashMap<>();

	private PidsRenderCalibration() {
	}

	public static Calibration get(String pidsType) {
		if (!RenderCalibrationFeature.isEnabled()) {
			return Calibration.DEFAULT;
		}
		return INSTANCE.calibrations.getOrDefault(pidsType, Calibration.DEFAULT);
	}

	public static void set(String pidsType, Calibration calibration) {
		if (!RenderCalibrationFeature.isEnabled()) {
			return;
		}
		if (calibration.equals(Calibration.DEFAULT)) {
			INSTANCE.calibrations.remove(pidsType);
		} else {
			INSTANCE.calibrations.put(pidsType, calibration);
		}
	}

	public static void save() {
		if (!RenderCalibrationFeature.isEnabled()) {
			return;
		}
		Properties properties = new Properties();
		INSTANCE.calibrations.forEach((pidsType, calibration) -> {
			writeTransform(properties, pidsType + ".front", calibration.front());
			writeTransform(properties, pidsType + ".back", calibration.back());
		});

		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (var output = Files.newOutputStream(CONFIG_PATH)) {
				properties.store(output, "MSD PIDS renderer calibration");
			}
		} catch (IOException exception) {
			throw new UncheckedIOException("Failed to save PIDS render calibration", exception);
		}
	}

	private static PidsRenderCalibration load() {
		PidsRenderCalibration calibration = new PidsRenderCalibration();
		if (!RenderCalibrationFeature.isEnabled() || !Files.exists(CONFIG_PATH)) {
			return calibration;
		}

		Properties properties = new Properties();
		try (var input = Files.newInputStream(CONFIG_PATH)) {
			properties.load(input);
		} catch (IOException exception) {
			throw new UncheckedIOException("Failed to read PIDS render calibration", exception);
		}

		for (String key : properties.stringPropertyNames()) {
			if (!key.endsWith(".front.x")) {
				continue;
			}
			String pidsType = key.substring(0, key.length() - ".front.x".length());
			calibration.calibrations.put(pidsType, new Calibration(
					readTransform(properties, pidsType + ".front"),
					readTransform(properties, pidsType + ".back")));
		}
		return calibration;
	}

	private static void writeTransform(Properties properties, String prefix, Transform transform) {
		properties.setProperty(prefix + ".x", Float.toString(transform.x()));
		properties.setProperty(prefix + ".y", Float.toString(transform.y()));
		properties.setProperty(prefix + ".z", Float.toString(transform.z()));
		properties.setProperty(prefix + ".tilt", Float.toString(transform.tilt()));
	}

	private static Transform readTransform(Properties properties, String prefix) {
		return new Transform(
				readFloat(properties, prefix + ".x"),
				readFloat(properties, prefix + ".y"),
				readFloat(properties, prefix + ".z"),
				readFloat(properties, prefix + ".tilt"));
	}

	private static float readFloat(Properties properties, String key) {
		try {
			return Float.parseFloat(properties.getProperty(key, "0"));
		} catch (NumberFormatException ignored) {
			return 0F;
		}
	}

	public record Transform(float x, float y, float z, float tilt) {
		public static final Transform ZERO = new Transform(0F, 0F, 0F, 0F);
	}

	public record Calibration(Transform front, Transform back) {
		public static final Calibration DEFAULT = new Calibration(Transform.ZERO, Transform.ZERO);

		public Transform transform(boolean backFace) {
			return backFace ? back : front;
		}
	}
}
