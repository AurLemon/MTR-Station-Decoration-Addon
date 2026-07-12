package top.mcmtr.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.mtr.registry.RegistryClient;
import top.mcmtr.block.OldNodeBlock;
import top.mcmtr.packet.MSDPacketUpdateCatenaryNode;

public class CatenaryScreen extends Screen {

	private final BlockPos blockPos;
	private final Screen previousScreen;
	private final boolean isConnected;
	private final EditBox offsetXField;
	private final EditBox offsetYField;
	private final EditBox offsetZField;

	public CatenaryScreen(BlockPos blockPos, boolean isConnected, OldNodeBlock.OldNodeBlockEntity blockEntity) {
		super(Component.translatable("gui.msd.catenary_offset"));
		this.blockPos = blockPos;
		this.previousScreen = Minecraft.getInstance().screen;
		this.isConnected = isConnected;
		this.offsetXField = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 20, Component.literal("offsetX"));
		this.offsetYField = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 20, Component.literal("offsetY"));
		this.offsetZField = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 20, Component.literal("offsetZ"));
		offsetXField.setValue(String.valueOf(blockEntity.getOffsetX()));
		offsetYField.setValue(String.valueOf(blockEntity.getOffsetY()));
		offsetZField.setValue(String.valueOf(blockEntity.getOffsetZ()));
	}

	@Override
	protected void init() {
		super.init();
		clearWidgets();

		final int panelWidth = Math.min(width - 40, 360);
		final int startX = (width - panelWidth) / 2;
		int y = 50;

		configureField(offsetXField, startX, y + 12, panelWidth);
		addRenderableWidget(offsetXField);
		y += 36;
		configureField(offsetYField, startX, y + 12, panelWidth);
		addRenderableWidget(offsetYField);
		y += 36;
		configureField(offsetZField, startX, y + 12, panelWidth);
		addRenderableWidget(offsetZField);

		y += 54;
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> saveAndClose())
				.bounds(startX, y, (panelWidth - 10) / 2, 20)
				.build());
		addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
				.bounds(startX + (panelWidth - 10) / 2 + 10, y, (panelWidth - 10) / 2, 20)
				.build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		final int panelWidth = Math.min(width - 40, 360);
		final int startX = (width - panelWidth) / 2;
		int y = 22;

		guiGraphics.drawCenteredString(font, title, width / 2, y, 0xFFFFFF);
		y += 28;
		guiGraphics.drawString(font, "offsetX", startX, y, 0xC0C0C0, false);
		y += 36;
		guiGraphics.drawString(font, "offsetY", startX, y, 0xC0C0C0, false);
		y += 36;
		guiGraphics.drawString(font, "offsetZ", startX, y, 0xC0C0C0, false);
		if (isConnected) {
			guiGraphics.drawString(font, "Connected catenary preview is not migrated yet.", startX, height - 28, 0x909090, false);
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
		RegistryClient.sendPacketToServer(new MSDPacketUpdateCatenaryNode(
				blockPos,
				parse(offsetXField.getValue(), -1, 1),
				parse(offsetYField.getValue(), -1, 1),
				parse(offsetZField.getValue(), -1, 1)));
		onClose();
	}

	private static void configureField(EditBox editBox, int x, int y, int width) {
		editBox.setX(x);
		editBox.setY(y);
		editBox.setWidth(width);
		editBox.setMaxLength(32);
	}

	private static double parse(String text, double minValue, double maxValue) {
		try {
			return Math.max(minValue, Math.min(maxValue, Double.parseDouble(text)));
		} catch (NumberFormatException ignored) {
			return 0;
		}
	}
}
