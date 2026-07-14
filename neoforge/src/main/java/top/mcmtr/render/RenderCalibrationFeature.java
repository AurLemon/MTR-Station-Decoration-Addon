package top.mcmtr.render;

public final class RenderCalibrationFeature {

	private static final String RESOURCE_PATH = "/assets/msd/render_calibration_enabled.flag";
	private static final boolean ENABLED = RenderCalibrationFeature.class.getResource(RESOURCE_PATH) != null;

	private RenderCalibrationFeature() {
	}

	public static boolean isEnabled() {
		return ENABLED;
	}
}
