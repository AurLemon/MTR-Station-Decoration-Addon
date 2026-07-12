package top.mcmtr.core.simulation;

import org.mtr.core.data.Position;
import org.mtr.core.serializer.SerializedDataBaseWithId;
import org.mtr.core.servlet.MessageQueue;
import org.mtr.core.servlet.QueueObject;
import org.mtr.core.simulation.FileLoader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectLongImmutablePair;
import top.mcmtr.core.MSDMain;
import top.mcmtr.core.data.Catenary;
import top.mcmtr.core.data.MSDData;
import top.mcmtr.core.data.RigidCatenary;
import top.mcmtr.core.legacy.data.LegacyCatenaryLoader;
import top.mcmtr.core.servlet.MSDOperationProcessor;

import java.nio.file.Path;

public class MSDSimulator extends MSDData implements Utilities {
    private boolean autoSave = false;
    private final String dimension;
    private final FileLoader<Catenary> fileLoaderCatenaries;
    private final FileLoader<RigidCatenary> fileLoaderRigidCatenaries;
    private final MessageQueue<Runnable> queuedRuns = new MessageQueue<>();
    private final MessageQueue<QueueObject> messageQueueC2S = new MessageQueue<>();
    private static final String KEY_CATENARIES = "catenaries";
    private static final String KEY_RIGID_CATENARIES = "rigid_catenaries";

    public MSDSimulator(String dimension, Path rootPath) {
        this.dimension = dimension;
        final long startMillis = System.currentTimeMillis();
        final Path savePath = rootPath.resolve(dimension);

        LegacyCatenaryLoader.loadCatenary(savePath, catenaries);
        LegacyCatenaryLoader.loadTransCatenary(savePath, catenaries);
        LegacyCatenaryLoader.loadRigidCatenary(savePath, rigidCatenaries);

        this.fileLoaderCatenaries = new FileLoader<>(catenaries, Catenary::new, savePath, KEY_CATENARIES);
        this.fileLoaderRigidCatenaries = new FileLoader<>(rigidCatenaries, RigidCatenary::new, savePath, KEY_RIGID_CATENARIES);

        final long endMillis = System.currentTimeMillis();
        MSDMain.MSD_CORE_LOG.debug("MSD Data loading complete for {} in {} second(s)", dimension, (float) (endMillis - startMillis) / MILLIS_PER_SECOND);
        sync();
    }

    public void tick() {
        try {
            if (autoSave) {
                save(true);
                autoSave = false;
            }

            queuedRuns.process(Runnable::run);
            messageQueueC2S.process(queueObject -> queueObject.runCallback(MSDOperationProcessor.process(queueObject.key, queueObject.data, this)));
        } catch (Exception e) {
            MSDMain.MSD_CORE_LOG.error("MSD Simulator tick error", e);
            throw e;
        }
    }

    public void save() {
        autoSave = true;
    }

    public void stop() {
        save(false);
    }

    public void run(Runnable runnable) {
        queuedRuns.put(runnable);
    }

    public void sendMessageC2S(QueueObject queueObject) {
        messageQueueC2S.put(queueObject);
    }

    public RigidCatenary getRigidCatenary(Position position) {
        return positionsToRigidCatenary.getOrDefault(position, new org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>())
                .values()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public Catenary getCatenary(Position position) {
        return positionsToCatenary.getOrDefault(position, new org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>())
                .values()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private void save(boolean useReducedHash) {
        final ObjectLongImmutablePair<Boolean> changedAndDuration = Utilities.measureDuration(() -> {
            final boolean changed1 = save(fileLoaderCatenaries, useReducedHash);
            final boolean changed2 = save(fileLoaderRigidCatenaries, useReducedHash);
            return changed1 || changed2;
        });
        if (changedAndDuration.left() || !useReducedHash) {
            MSDMain.MSD_CORE_LOG.debug("MSD Save complete for {} in {} second(s)", dimension, changedAndDuration.rightLong() / 1000F);
        }
    }

    private <T extends SerializedDataBaseWithId> boolean save(FileLoader<T> fileLoader, boolean useReducedHash) {
        final IntIntImmutablePair saveCounts = fileLoader.save(useReducedHash);
        final int changedCount = saveCounts.leftInt();
        if (changedCount > 0) {
            MSDMain.MSD_CORE_LOG.debug("- MSD Changed {}: {}", fileLoader.key, saveCounts.leftInt());
        }
        final int deletedCount = saveCounts.rightInt();
        if (deletedCount > 0) {
            MSDMain.MSD_CORE_LOG.debug("- MSD Deleted {}: {}", fileLoader.key, saveCounts.rightInt());
        }
        return changedCount > 0 || deletedCount > 0;
    }
}
