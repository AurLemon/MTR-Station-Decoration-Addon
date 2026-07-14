package top.mcmtr.screen;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import top.mcmtr.render.PidsRenderCalibration;

public final class PidsRenderCalibrationScreen extends Screen {

	private static final String[] AXES = {"X", "Y", "Z", "Tilt"};

	private final String pidsType;
	private final Screen returnScreen;
	private final PidsRenderCalibration.Calibration originalCalibration;
	private final EditBox[] frontBoxes = new EditBox[4];
	private final EditBox[] backBoxes = new EditBox[4];
	private boolean saved;

	public PidsRenderCalibrationScreen(String pidsType, Screen returnScreen) {
		super(Component.literal("PIDS Render Calibration"));
		this.pidsType = pidsType;
		this.returnScreen = returnScreen;
		this.originalCalibration = PidsRenderCalibration.get(pidsType);
	}

	@Override
	protected void init() {
		int left = width / 2 - 156;
		int top = height / 2 - 92;
		PidsRenderCalibration.Calibration currentCalibration = PidsRenderCalibration.get(pidsType);
		addTransformInputs(frontBoxes, left, top + 36, currentCalibration.front());
		addTransformInputs(backBoxes, left + 160, top + 36, currentCalibration.back());

		addRenderableWidget(Button.builder(Component.literal("Reset"), button -> reset())
				.pos(left, top + 146)
				.size(96, 20)
				.build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
				.pos(left + 104, top + 146)
				.size(96, 20)
				.build());
		addRenderableWidget(Button.builder(Component.literal("Apply & Save"), button -> save())
				.pos(left + 208, top + 146)
				.size(104, 20)
				.build());
		setInitialFocus(frontBoxes[0]);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		int left = width / 2 - 156;
		int top = height / 2 - 92;
		guiGraphics.drawCenteredString(font, title, width / 2, top - 20, 0xFFFFFF);
		guiGraphics.drawCenteredString(font, Component.literal(pidsType + " (delta)"), width / 2, top - 8, 0xA0A0A0);
		guiGraphics.drawString(font, Component.literal("Front"), left, top + 18, 0xFFFFFF, false);
		guiGraphics.drawString(font, Component.literal("Back"), left + 160, top + 18, 0xFFFFFF, false);
		for (int index = 0; index < AXES.length; index++) {
			int y = top + 40 + index * 26;
			guiGraphics.drawString(font, Component.literal(AXES[index]), left, y - 10, 0xA0A0A0, false);
			guiGraphics.drawString(font, Component.literal(AXES[index]), left + 160, y - 10, 0xA0A0A0, false);
		}
	}

	@Override
	public void onClose() {
		if (!saved) {
			PidsRenderCalibration.set(pidsType, originalCalibration);
		}
		Minecraft.getInstance().setScreen(returnScreen);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		EditBox hoveredBox = getHoveredBox(mouseX, mouseY);
		if (hoveredBox == null || scrollY == 0.0D) {
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		double step = hasShiftDown() ? 0.001D : hasControlDown() ? 0.05D : 0.005D;
		float value = readFloat(hoveredBox, 0.0F);
		hoveredBox.setValue(format((float) (value + Math.signum(scrollY) * step)));
		hoveredBox.setFocused(true);
		applyPreview();
		return true;
	}

	private void addTransformInputs(EditBox[] boxes, int x, int y, PidsRenderCalibration.Transform transform) {
		float[] values = {transform.x(), transform.y(), transform.z(), transform.tilt()};
		for (int index = 0; index < boxes.length; index++) {
			EditBox box = new EditBox(font, x, y + index * 26, 144, 20, Component.literal(AXES[index]));
			box.setMaxLength(16);
			box.setValue(format(values[index]));
			box.setResponder(ignored -> applyPreview());
			boxes[index] = addRenderableWidget(box);
		}
	}

	private void reset() {
		setTransformValues(frontBoxes, PidsRenderCalibration.Transform.ZERO);
		setTransformValues(backBoxes, PidsRenderCalibration.Transform.ZERO);
		applyPreview();
	}

	private void save() {
		applyPreview();
		PidsRenderCalibration.save();
		saved = true;
		onClose();
	}

	private void applyPreview() {
		PidsRenderCalibration.set(pidsType, new PidsRenderCalibration.Calibration(
				readTransform(frontBoxes, PidsRenderCalibration.Transform.ZERO),
				readTransform(backBoxes, PidsRenderCalibration.Transform.ZERO)));
	}

	private EditBox getHoveredBox(double mouseX, double mouseY) {
		for (EditBox box : frontBoxes) {
			if (box.isMouseOver(mouseX, mouseY)) {
				return box;
			}
		}
		for (EditBox box : backBoxes) {
			if (box.isMouseOver(mouseX, mouseY)) {
				return box;
			}
		}
		return null;
	}

	private static PidsRenderCalibration.Transform readTransform(EditBox[] boxes, PidsRenderCalibration.Transform fallback) {
		return new PidsRenderCalibration.Transform(
				readFloat(boxes[0], fallback.x()),
				readFloat(boxes[1], fallback.y()),
				readFloat(boxes[2], fallback.z()),
				readFloat(boxes[3], fallback.tilt()));
	}

	private static float readFloat(EditBox box, float fallback) {
		try {
			return Float.parseFloat(box.getValue().trim());
		} catch (NumberFormatException ignored) {
			return fallback;
		}
	}

	private static void setTransformValues(EditBox[] boxes, PidsRenderCalibration.Transform transform) {
		boxes[0].setValue(format(transform.x()));
		boxes[1].setValue(format(transform.y()));
		boxes[2].setValue(format(transform.z()));
		boxes[3].setValue(format(transform.tilt()));
	}

	private static String format(float value) {
		return String.format(Locale.ROOT, "%.4f", value);
	}
}
