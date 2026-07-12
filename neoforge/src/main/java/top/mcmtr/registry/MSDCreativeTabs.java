package top.mcmtr.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.mcmtr.init.MSDNeoForge;

public final class MSDCreativeTabs {

	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MSDNeoForge.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STATION =
			CREATIVE_MODE_TABS.register("station", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.msd.station"))
					.icon(() -> new ItemStack(MSDBlocks.YUUNI_PIDS.asItem()))
					.displayItems((parameters, output) -> {
						MSDItems.addStationTabItems(output::accept);
						MSDBlocks.addStationTabItems(output::accept);
					})
					.build());
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXTERNAL =
			CREATIVE_MODE_TABS.register("external", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.msd.external"))
					.icon(() -> new ItemStack(MSDItems.CATENARY_CONNECTOR.get()))
					.displayItems((parameters, output) -> {
						MSDItems.addExternalTabItems(output::accept);
						MSDBlocks.addExternalTabItems(output::accept);
					})
					.build());

	private MSDCreativeTabs() {
	}

	public static void register(IEventBus modEventBus) {
		CREATIVE_MODE_TABS.register(modEventBus);
	}
}
