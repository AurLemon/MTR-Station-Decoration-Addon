package top.mcmtr.screen;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.registry.RegistryClient;
import top.mcmtr.block.YamanoteRailwaySignBlock;
import top.mcmtr.packet.MSDPacketUpdateYamanoteRailwaySignConfig;

public class YamanoteRailwaySignScreen extends Screen {

	private final BlockPos blockPos;
	private final int length;
	private final Screen previousScreen;
	private final EditBox[] signIdFields;
	private final String[] signIds;
	private final LongAVLTreeSet selectedIds;

	private EditBox selectedIdsField;

	public YamanoteRailwaySignScreen(
			BlockPos blockPos,
			YamanoteRailwaySignBlock signBlock,
			YamanoteRailwaySignBlock.YamanoteRailwaySignBlockEntity signBlockEntity) {
		super(Component.translatable("block.msd.yamanote_railway_sign"));
		this.blockPos = blockPos;
		this.length = signBlock.getLength();
		this.signIds = signBlockEntity.getSignIds().clone();
		this.selectedIds = new LongAVLTreeSet(signBlockEntity.getSelectedIds());
		this.previousScreen = Minecraft.getInstance().screen;
		this.signIdFields = new EditBox[length];
	}

	@Override
	protected void init() {
		super.init();
		clearWidgets();

		final int panelWidth = Math.min(width - 40, 420);
		final int startX = (width - panelWidth) / 2;
		int y = 50;

		selectedIdsField = new EditBox(font, startX, y + 12, panelWidth, 20, Component.literal("selectedIds"));
		selectedIdsField.setValue(formatSelectedIds(selectedIds));
		addRenderableWidget(selectedIdsField);

		y += 44;
		for (int i = 0; i < signIdFields.length; i++) {
			final EditBox field = new EditBox(font, startX, y + 12, panelWidth, 20, Component.literal("signId[" + i + "]"));
			field.setMaxLength(128);
			field.setValue(signIds[i] == null ? "" : signIds[i]);
			signIdFields[i] = field;
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
		guiGraphics.drawString(font, "selectedIds (comma separated)", startX, y, 0xC0C0C0, false);
		y += 44;

		for (int i = 0; i < signIdFields.length; i++) {
			guiGraphics.drawString(font, "signId[" + i + "]", startX, y, 0xC0C0C0, false);
			y += 36;
		}

		guiGraphics.drawString(font, "Use lower-case sign ids, leave blank to clear a slot.", startX, height - 28, 0x909090, false);
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
		final LongAVLTreeSet updatedSelectedIds = parseSelectedIds(selectedIdsField.getValue());
		final String[] updatedSignIds = new String[length];
		for (int i = 0; i < signIdFields.length; i++) {
			final String value = signIdFields[i].getValue().trim().toLowerCase(Locale.ENGLISH);
			updatedSignIds[i] = value.isEmpty() ? null : value;
		}

		RegistryClient.sendPacketToServer(new MSDPacketUpdateYamanoteRailwaySignConfig(blockPos, updatedSelectedIds, updatedSignIds));
		onClose();
	}

	private static String formatSelectedIds(LongAVLTreeSet selectedIds) {
		return selectedIds.longStream().mapToObj(String::valueOf).collect(Collectors.joining(","));
	}

	private static LongAVLTreeSet parseSelectedIds(String value) {
		final LongAVLTreeSet result = new LongAVLTreeSet();
		if (value == null || value.isBlank()) {
			return result;
		}

		Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(token -> !token.isEmpty())
				.forEach(token -> {
					try {
						result.add(Long.parseLong(token));
					} catch (NumberFormatException ignored) {
					}
				});
		return result;
	}
}
