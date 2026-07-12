package top.mcmtr.init;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mtr.core.data.Position;
import org.mtr.core.servlet.QueueObject;
import top.mcmtr.core.data.Catenary;
import top.mcmtr.core.MSDMain;
import top.mcmtr.core.data.MSDData;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.operation.MSDDeleteDataRequest;
import top.mcmtr.core.operation.MSDUpdateDataRequest;
import top.mcmtr.core.servlet.OperationType;

import javax.annotation.Nullable;

public final class MSDNeoForgeRuntime {

	private static final Logger LOGGER = LoggerFactory.getLogger("MTR Station Decoration Addon Runtime");
	private static final int AUTOSAVE_INTERVAL_MILLIS = 30_000;
	private static final boolean USE_THREADED_SIMULATION = false;
	private static MSDNeoForgeRuntime instance;

	private MSDMain main;
	private long lastSavedMillis;
	private String[] dimensions = new String[0];

	public MSDNeoForgeRuntime() {
		instance = this;
		NeoForge.EVENT_BUS.addListener(this::onServerStarted);
		NeoForge.EVENT_BUS.addListener(this::onServerTickPost);
		NeoForge.EVENT_BUS.addListener(this::onServerStopping);
	}

	@Nullable
	public static MSDNeoForgeRuntime getInstance() {
		return instance;
	}

	private void onServerStarted(ServerStartedEvent event) {
		final var server = event.getServer();
		final java.util.ArrayList<String> dimensions = new java.util.ArrayList<>();
		for (final ServerLevel level : server.getAllLevels()) {
			final ResourceLocation location = level.dimension().location();
			dimensions.add(location.getNamespace() + "/" + location.getPath());
		}

		this.dimensions = dimensions.toArray(String[]::new);
		lastSavedMillis = System.currentTimeMillis();
		main = new MSDMain(
				server.getWorldPath(LevelResource.ROOT).resolve("msd"),
				USE_THREADED_SIMULATION,
				this.dimensions
		);
		LOGGER.info("Initialized MSD NeoForge runtime for {} dimension(s)", dimensions.size());
	}

	private void onServerTickPost(ServerTickEvent.Post event) {
		if (main == null) {
			return;
		}

		if (!USE_THREADED_SIMULATION) {
			main.manualTick();
		}

		final long currentMillis = System.currentTimeMillis();
		if (currentMillis - lastSavedMillis > AUTOSAVE_INTERVAL_MILLIS) {
			main.save();
			lastSavedMillis = currentMillis;
		}
	}

	private void onServerStopping(ServerStoppingEvent event) {
		if (main == null) {
			return;
		}

		main.stop();
		main = null;
		dimensions = new String[0];
		LOGGER.info("Stopped MSD NeoForge runtime");
	}

	public MSDData newClientData() {
		return new MSDData() {
		};
	}

	public boolean sendUpdate(ServerLevel level, MSDUpdateDataRequest request) {
		return send(level, OperationType.UPDATE_DATA, request);
	}

	public boolean sendDelete(ServerLevel level, MSDDeleteDataRequest request) {
		return send(level, OperationType.DELETE_DATA, request);
	}

	public boolean saveNow() {
		if (main == null) {
			return false;
		}
		main.save();
		lastSavedMillis = System.currentTimeMillis();
		return true;
	}

	@Nullable
	public RigidCatenary getRigidCatenary(ServerLevel level, BlockPos pos) {
		if (main == null) {
			return null;
		}

		final int worldIndex = getWorldIndex(level);
		if (worldIndex < 0) {
			return null;
		}

		return main.getRigidCatenary(worldIndex, new Position(pos.getX(), pos.getY(), pos.getZ()));
	}

	@Nullable
	public Catenary getCatenary(ServerLevel level, BlockPos pos) {
		if (main == null) {
			return null;
		}

		final int worldIndex = getWorldIndex(level);
		if (worldIndex < 0) {
			return null;
		}

		return main.getCatenary(worldIndex, new Position(pos.getX(), pos.getY(), pos.getZ()));
	}

	private boolean send(ServerLevel level, String key, Object request) {
		if (main == null || !(request instanceof org.mtr.core.serializer.SerializedDataBase serializedDataBase)) {
			return false;
		}

		final int worldIndex = getWorldIndex(level);
		if (worldIndex < 0) {
			return false;
		}

		main.sendMessageC2S(worldIndex, new QueueObject(key, serializedDataBase, null, null));
		if (!USE_THREADED_SIMULATION) {
			main.manualTick();
		}
		return true;
	}

	private int getWorldIndex(ServerLevel level) {
		final ResourceLocation location = level.dimension().location();
		final String worldId = location.getNamespace() + "/" + location.getPath();
		for (int i = 0; i < dimensions.length; i++) {
			if (dimensions[i].equals(worldId)) {
				return i;
			}
		}
		return -1;
	}
}
