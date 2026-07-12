package top.mcmtr.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.mtr.registry.RegistryClient;
import top.mcmtr.block.AbstractStandingSignBlock;
import top.mcmtr.packet.MSDPacketUpdateCustomText;

public class CustomTextScreen extends Screen {

	private static final int MAX_MESSAGE_LENGTH = 2048;

	private final BlockPos blockPos;
	private final Screen previousScreen;
	private final String[] messages;
	private final EditBox[] textFieldMessages;

	public CustomTextScreen(
			BlockPos blockPos,
			int maxArrivals,
			AbstractStandingSignBlock.StandingSignBlockEntity blockEntity) {
		super(Component.translatable("gui.msd.custom_text"));
		this.blockPos = blockPos;
		this.previousScreen = Minecraft.getInstance().screen;
		this.messages = new String[maxArrivals];
		this.textFieldMessages = new EditBox[maxArrivals];
		for (int i = 0; i < maxArrivals; i++) {
			messages[i] = blockEntity.getMessage(i);
		}
	}

	@Override
	protected void init() {
		super.init();
		clearWidgets();

		final int panelWidth = Math.min(width - 40, 420);
		final int startX = (width - panelWidth) / 2;
		int y = 50;

		for (int i = 0; i < textFieldMessages.length; i++) {
			final EditBox field = new EditBox(font, startX, y + 12, panelWidth, 20, Component.literal("message[" + i + "]"));
			field.setMaxLength(MAX_MESSAGE_LENGTH);
			field.setValue(messages[i] == null ? "" : messages[i]);
			textFieldMessages[i] = field;
			addRenderableWidget(field);
			y += 36;
		}

		addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> saveAndClose())
				.bounds(startX, y + 18, (panelWidth - 10) / 2, 20)
				.build());
		addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
				.bounds(startX + (panelWidth - 10) / 2 + 10, y + 18, (panelWidth - 10) / 2, 20)
				.build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		final int panelWidth = Math.min(width - 40, 420);
		final int startX = (width - panelWidth) / 2;
		int y = 22;

		guiGraphics.drawCenteredString(font, title, width / 2, y, 0xFFFFFF);
		y += 18;
		for (int i = 0; i < textFieldMessages.length; i++) {
			guiGraphics.drawString(font, "message[" + i + "]", startX, y, 0xC0C0C0, false);
			y += 36;
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
		final String[] updatedMessages = new String[textFieldMessages.length];
		for (int i = 0; i < textFieldMessages.length; i++) {
			updatedMessages[i] = textFieldMessages[i].getValue();
		}
		RegistryClient.sendPacketToServer(new MSDPacketUpdateCustomText(blockPos, updatedMessages));
		onClose();
	}
}
