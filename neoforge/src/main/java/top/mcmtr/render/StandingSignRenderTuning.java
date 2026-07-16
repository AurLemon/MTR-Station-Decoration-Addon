package top.mcmtr.render;

public record StandingSignRenderTuning(
		float yawCorrectionDegrees,
		float translateX,
		float translateY,
		float translateZ,
		float frontDepthNudge,
		float backDepthNudge,
		boolean renderBackText,
		boolean seeThroughText) {

	private static final StandingSignRenderTuning DEFAULT = new StandingSignRenderTuning(0F, 0F, 0F, 0F, -0.0025F, 0.0025F, true, false);
	private static final StandingSignRenderTuning CALIBRATION = DEFAULT;

	public static StandingSignRenderTuning active() {
		return switch (StandingSignRenderGeneratedPreset.ACTIVE) {
			case "calibration" -> CALIBRATION;
			default -> DEFAULT;
		};
	}
}
