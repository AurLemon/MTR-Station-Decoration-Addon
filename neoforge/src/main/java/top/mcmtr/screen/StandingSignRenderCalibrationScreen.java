package top.mcmtr.screen;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import top.mcmtr.render.StandingSignRenderCalibration;

public final class StandingSignRenderCalibrationScreen extends Screen {

	private static final String[] AXES = {"Yaw", "X", "Y", "Z", "Front Z", "Back Z"};

	private final String signType;
	private final Screen returnScreen;
	private final StandingSignRenderCalibration.Transform originalTransform;
	private final EditBox[] boxes = new EditBox[6];
	private boolean saved;

	public StandingSignRenderCalibrationScreen(String signType, Screen returnScreen) {
		super(Component.literal("Standing Sign Render Calibration"));
		this.signType = signType;
		this.returnScreen = returnScreen;
		this.originalTransform = StandingSignRenderCalibration.get(signType);
	}

	@Override
	protected void init() {
		int left = width / 2 - 72;
		int top = height / 2 - 96;
		StandingSignRenderCalibration.Transform transform = StandingSignRenderCalibration.get(signType);
		float[] values = {transform.yaw(), transform.x(), transform.y(), transform.z(), transform.frontDepth(), transform.backDepth()};
		for (int index = 0; index < boxes.length; index++) {
			EditBox box = new EditBox(font, left, top + index * 26, 144, 20, Component.literal(AXES[index]));
			box.setMaxLength(16);
			box.setValue(format(values[index]));
			box.setResponder(ignored -> applyPreview());
			boxes[index] = addRenderableWidget(box);
		}
		addRenderableWidget(Button.builder(Component.literal("Reset"), button -> reset())
				.pos(left, top + 164)
				.size(46, 20)
				.build());
		addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
				.pos(left + 49, top + 164)
				.size(46, 20)
				.build());
		addRenderableWidget(Button.builder(Component.literal("Save"), button -> save())
				.pos(left + 98, top + 164)
				.size(46, 20)
				.build());
		setInitialFocus(boxes[0]);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		int left = width / 2 - 72;
		int top = height / 2 - 96;
		guiGraphics.drawCenteredString(font, title, width / 2, top - 24, 0xFFFFFF);
		guiGraphics.drawCenteredString(font, Component.literal(signType + " (delta)"), width / 2, top - 12, 0xA0A0A0);
		for (int index = 0; index < AXES.length; index++) {
			guiGraphics.drawString(font, Component.literal(AXES[index]), left, top + index * 26 - 10, 0xA0A0A0, false);
		}
	}

	@Override
	public void onClose() {
		if (!saved) {
			StandingSignRenderCalibration.set(signType, originalTransform);
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

	private void reset() {
		for (EditBox box : boxes) {
			box.setValue(format(0F));
		}
		applyPreview();
	}

	private void save() {
		applyPreview();
		StandingSignRenderCalibration.save();
		saved = true;
		onClose();
	}

	private void applyPreview() {
		StandingSignRenderCalibration.set(signType, new StandingSignRenderCalibration.Transform(
				readFloat(boxes[0], 0F),
				readFloat(boxes[1], 0F),
				readFloat(boxes[2], 0F),
				readFloat(boxes[3], 0F),
				readFloat(boxes[4], 0F),
				readFloat(boxes[5], 0F)));
	}

	private EditBox getHoveredBox(double mouseX, double mouseY) {
		for (EditBox box : boxes) {
			if (box.isMouseOver(mouseX, mouseY)) {
				return box;
			}
		}
		return null;
	}

	private static float readFloat(EditBox box, float fallback) {
		try {
			return Float.parseFloat(box.getValue().trim());
		} catch (NumberFormatException ignored) {
			return fallback;
		}
	}

	private static String format(float value) {
		return String.format(Locale.ROOT, "%.4f", value);
	}
}
