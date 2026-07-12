package top.mcmtr.registry;

import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.mcmtr.core.data.CatenaryType;
import top.mcmtr.item.CatenaryConnectorItem;
import top.mcmtr.init.MSDNeoForge;
import top.mcmtr.item.RigidCatenaryConnectorItem;

public final class MSDItems {

	private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MSDNeoForge.MOD_ID);

	public static final DeferredHolder<Item, Item> CATENARY_CONNECTOR =
			ITEMS.register("catenary_connector", () -> new CatenaryConnectorItem(new Item.Properties(), true, CatenaryType.CATENARY));
	public static final DeferredHolder<Item, Item> ELECTRIC_CONNECTOR =
			ITEMS.register("electric_connector", () -> new CatenaryConnectorItem(new Item.Properties(), true, CatenaryType.ELECTRIC));
	public static final DeferredHolder<Item, Item> RIGID_SOFT_CATENARY_CONNECTOR =
			ITEMS.register("rigid_soft_catenary_connector", () -> new CatenaryConnectorItem(new Item.Properties(), true, CatenaryType.RIGID_SOFT_CATENARY));
	public static final DeferredHolder<Item, Item> RIGID_CATENARY_CONNECTOR =
			ITEMS.register("rigid_catenary_connector", () -> new RigidCatenaryConnectorItem(new Item.Properties(), true));
	public static final DeferredHolder<Item, Item> CATENARY_REMOVER =
			ITEMS.register("catenary_remover", () -> new CatenaryConnectorItem(new Item.Properties(), false, CatenaryType.NONE));
	public static final DeferredHolder<Item, Item> RIGID_CATENARY_REMOVER =
			ITEMS.register("rigid_catenary_remover", () -> new RigidCatenaryConnectorItem(new Item.Properties(), false));

	private MSDItems() {
	}

	public static void register(IEventBus modEventBus) {
		ITEMS.register(modEventBus);
	}

	public static void addCreativeTabItems(Consumer<Item> consumer) {
		consumer.accept(CATENARY_CONNECTOR.get());
		consumer.accept(ELECTRIC_CONNECTOR.get());
		consumer.accept(RIGID_SOFT_CATENARY_CONNECTOR.get());
		consumer.accept(RIGID_CATENARY_CONNECTOR.get());
		consumer.accept(CATENARY_REMOVER.get());
		consumer.accept(RIGID_CATENARY_REMOVER.get());
	}

	public static void addStationTabItems(Consumer<Item> consumer) {
	}

	public static void addExternalTabItems(Consumer<Item> consumer) {
		addCreativeTabItems(consumer);
	}
}
