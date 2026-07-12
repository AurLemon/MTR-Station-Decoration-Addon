package top.mcmtr.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.core.data.Position;
import org.mtr.core.servlet.QueueObject;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import top.mcmtr.core.data.Catenary;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.simulation.MSDSimulator;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MSDMain {
    private final ObjectImmutableList<MSDSimulator> simulators;
    private final ScheduledExecutorService scheduledExecutorService;
    public static final int MILLISECONDS_PER_TICK = 10;
    public static final Logger MSD_CORE_LOG = LogManager.getLogger("MSD_SERVER_LOGGER");

    public MSDMain(Path rootPath, boolean threadedSimulation, String... dimensions) {
        final ObjectArrayList<MSDSimulator> tempSimulators = new ObjectArrayList<>();

        MSD_CORE_LOG.info("MSD server Loading files...");
        for (final String dimension : dimensions) {
            tempSimulators.add(new MSDSimulator(dimension, rootPath));
        }

        simulators = new ObjectImmutableList<>(tempSimulators);

        if (threadedSimulation) {
            scheduledExecutorService = Executors.newScheduledThreadPool(simulators.size());
            simulators.forEach(simulator -> scheduledExecutorService.scheduleAtFixedRate(simulator::tick, 0, MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS));
        } else {
            scheduledExecutorService = null;
        }

        MSD_CORE_LOG.info("MSD server started with dimensions {}", Arrays.toString(dimensions));
    }

    public void manualTick() {
        simulators.forEach(MSDSimulator::tick);
    }

    public void sendMessageC2S(@Nullable Integer worldIndex, QueueObject queueObject) {
        if (worldIndex == null) {
            simulators.forEach(simulator -> simulator.sendMessageC2S(queueObject));
        } else if (worldIndex >= 0 && worldIndex < simulators.size()) {
            simulators.get(worldIndex).sendMessageC2S(queueObject);
        }
    }

    public void save() {
        simulators.forEach(MSDSimulator::save);
    }

    @Nullable
    public RigidCatenary getRigidCatenary(int worldIndex, Position position) {
        if (worldIndex < 0 || worldIndex >= simulators.size()) {
            return null;
        }
        return simulators.get(worldIndex).getRigidCatenary(position);
    }

    @Nullable
    public Catenary getCatenary(int worldIndex, Position position) {
        if (worldIndex < 0 || worldIndex >= simulators.size()) {
            return null;
        }
        return simulators.get(worldIndex).getCatenary(position);
    }

    public void stop() {
        MSD_CORE_LOG.info("MSD stopping...");
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            Utilities.awaitTermination(scheduledExecutorService);
        }
        MSD_CORE_LOG.info("MSD starting full save...");
        simulators.forEach(MSDSimulator::stop);
        MSD_CORE_LOG.info("MSD stopped");
    }
}
