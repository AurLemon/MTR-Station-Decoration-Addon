package top.mcmtr.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.core.tool.Utilities;
import org.mtr.registry.RegistryClient;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.packet.MSDPacketUpdateRigidCatenary;

public final class RigidCatenaryShapeModifierScreen extends Screen {

	private final Screen previousScreen;
	private final RigidCatenary originalRigidCatenary;
	private RigidCatenary.Shape shape;
	private double radius;
	private boolean dirty;

	private Button shapeButton;
	private Button minusTenButton;
	private Button minusOneButton;
	private Button minusPointOneButton;
	private Button plusPointOneButton;
	private Button plusOneButton;
	private Button plusTenButton;
	private EditBox radiusField;

	public RigidCatenaryShapeModifierScreen(RigidCatenary rigidCatenary) {
		super(Component.translatable("gui.msd.rigid_catenary_shape"));
		this.previousScreen = Minecraft.getInstance().screen;
		this.originalRigidCatenary = rigidCatenary;
		this.shape = rigidCatenary.rigidCatenaryMath.getShape();
		this.radius = rigidCatenary.rigidCatenaryMath.getVerticalRadius();
		this.dirty = false;
	}

	@Override
	protected void init() {
		super.init();
		clearWidgets();

		final int panelWidth = Math.min(width - 40, 420);
		final int startX = (width - panelWidth) / 2;
		final int firstRowY = 52;
		final int secondRowY = 102;
		final int buttonHeight = 20;
		final int smallButtonWidth = 52;
		final int gap = 6;

		shapeButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
			shape = shape == RigidCatenary.Shape.QUADRATIC ? RigidCatenary.Shape.TWO_RADII : RigidCatenary.Shape.QUADRATIC;
			updateRadius(radius, true);
		}).bounds(startX, firstRowY, panelWidth, buttonHeight).build());

		minusTenButton = addRenderableWidget(Button.builder(Component.literal("-10"), button -> updateRadius(radius - 10, true))
				.bounds(startX, secondRowY, smallButtonWidth, buttonHeight).build());
		minusOneButton = addRenderableWidget(Button.builder(Component.literal("-1"), button -> updateRadius(radius - 1, true))
				.bounds(startX + (smallButtonWidth + gap), secondRowY, smallButtonWidth, buttonHeight).build());
		minusPointOneButton = addRenderableWidget(Button.builder(Component.literal("-0.1"), button -> updateRadius(radius - 0.1, true))
				.bounds(startX + (smallButtonWidth + gap) * 2, secondRowY, smallButtonWidth, buttonHeight).build());

		final int fieldWidth = panelWidth - (smallButtonWidth + gap) * 6 - gap;
		radiusField = addRenderableWidget(new EditBox(font, startX + (smallButtonWidth + gap) * 3, secondRowY, fieldWidth, buttonHeight, Component.literal("radius")));
		radiusField.setResponder(value -> {
			if (radiusField.isFocused()) {
				try {
					updateRadius(Double.parseDouble(value), true);
				} catch (NumberFormatException ignored) {
				}
			}
		});

		final int plusStartX = startX + panelWidth - smallButtonWidth * 3 - gap * 2;
		plusPointOneButton = addRenderableWidget(Button.builder(Component.literal("+0.1"), button -> updateRadius(radius + 0.1, true))
				.bounds(plusStartX, secondRowY, smallButtonWidth, buttonHeight).build());
		plusOneButton = addRenderableWidget(Button.builder(Component.literal("+1"), button -> updateRadius(radius + 1, true))
				.bounds(plusStartX + smallButtonWidth + gap, secondRowY, smallButtonWidth, buttonHeight).build());
		plusTenButton = addRenderableWidget(Button.builder(Component.literal("+10"), button -> updateRadius(radius + 10, true))
				.bounds(plusStartX + (smallButtonWidth + gap) * 2, secondRowY, smallButtonWidth, buttonHeight).build());

		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> saveAndClose())
				.bounds(startX, secondRowY + 42, (panelWidth - 10) / 2, buttonHeight).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
				.bounds(startX + (panelWidth - 10) / 2 + 10, secondRowY + 42, (panelWidth - 10) / 2, buttonHeight).build());

		updateRadius(radius, false);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		final int panelWidth = Math.min(width - 40, 420);
		final int startX = (width - panelWidth) / 2;

		guiGraphics.drawCenteredString(font, title, width / 2, 22, 0xFFFFFF);
		guiGraphics.drawString(font, Component.translatable("gui.msd.rigid_catenary_shape"), startX, 38, 0xC0C0C0, false);
		if (shape != RigidCatenary.Shape.QUADRATIC) {
			guiGraphics.drawString(font, Component.translatable("gui.msd.rigid_catenary_radius"), startX, 88, 0xC0C0C0, false);
		}
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(previousScreen);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void saveAndClose() {
		if (dirty) {
			RegistryClient.sendPacketToServer(new MSDPacketUpdateRigidCatenary(RigidCatenary.copy(originalRigidCatenary, shape, radius)));
		}
		onClose();
	}

	private void updateRadius(double newRadius, boolean markDirty) {
		final double maxRadius = originalRigidCatenary.rigidCatenaryMath.getMaxVerticalRadius();
		radius = Utilities.clampSafe(Utilities.round(newRadius, 2), 0, maxRadius);
		if (markDirty) {
			dirty = true;
		}

		shapeButton.setMessage(Component.translatable(shape == RigidCatenary.Shape.QUADRATIC
				? "gui.msd.rigid_catenary_shape_quadratic"
				: "gui.msd.rigid_catenary_shape_two_radii"));

		final boolean showRadiusControls = shape != RigidCatenary.Shape.QUADRATIC;
		minusTenButton.visible = showRadiusControls;
		minusOneButton.visible = showRadiusControls;
		minusPointOneButton.visible = showRadiusControls;
		plusPointOneButton.visible = showRadiusControls;
		plusOneButton.visible = showRadiusControls;
		plusTenButton.visible = showRadiusControls;
		radiusField.visible = showRadiusControls;

		minusTenButton.active = radius > 0;
		minusOneButton.active = radius > 0;
		minusPointOneButton.active = radius > 0;
		plusPointOneButton.active = radius < maxRadius;
		plusOneButton.active = radius < maxRadius;
		plusTenButton.active = radius < maxRadius;

		if (!radiusField.isFocused()) {
			radiusField.setValue(String.valueOf(radius));
		}
	}
}
