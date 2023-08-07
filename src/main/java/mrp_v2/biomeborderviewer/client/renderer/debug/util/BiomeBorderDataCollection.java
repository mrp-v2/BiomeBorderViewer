package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mrp_v2.biomeborderviewer.client.Config;
import mrp_v2.biomeborderviewer.client.renderer.debug.VisualizeBorders;
import mrp_v2.biomeborderviewer.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BiomeBorderDataCollection {
    /**
     * Does not need synchronization
     */
    private final HashMap<Int3, CalculatedChunkData> calculatedChunks;
    /**
     * Does not need synchronization
     */
    private final HashSet<ChunkPos> loadedChunks;
    /**
     * Needs synchronization, use this as a lock
     */
    private final HashMap<Int3, CalculatedChunkData> calculatedChunksToAdd;
    /**
     * Needs synchronization, use {@link BiomeBorderDataCollection#calculatedChunksToAdd} as a lock
     */
    private final HashSet<Int3> chunksQueuedForCalculation;
    @Nullable
    private ExecutorService threadPool;
    private final int minChunkY;
    private final int maxChunkY;

    public BiomeBorderDataCollection(DimensionType dimensionType) {
        this.calculatedChunks = new HashMap<>();
        this.calculatedChunksToAdd = new HashMap<>();
        this.chunksQueuedForCalculation = new HashSet<>();
        this.loadedChunks = new HashSet<>();
        this.threadPool = null;
        minChunkY = dimensionType.minY() / 16;
        int maxY = dimensionType.minY() + dimensionType.height() - 1;
        maxChunkY = maxY / 16 - 1;
    }

    public void chunkLoaded(ChunkPos pos) {
        loadedChunks.add(pos);
    }

    public void chunkUnloaded(ChunkPos pos) {
        loadedChunks.remove(pos);
        for (int y = minChunkY; y <= maxChunkY; y++) {
            calculatedChunks.remove(new Int3(pos.x, y, pos.z));
        }
    }

    public void chunkCalculated(Int3 pos, CalculatedChunkData data) {
        synchronized (calculatedChunksToAdd) {
            calculatedChunksToAdd.put(pos, data);
            chunksQueuedForCalculation.remove(pos);
        }
    }

    public boolean areNoChunksLoaded() {
        return loadedChunks.isEmpty();
    }

    public void renderBorders(Int3[] chunksToRender, VertexConsumer bufferBuilder, Level world, double cameraX, double cameraY, double cameraZ) {
        HashSet<Int3> chunksToQueue = new HashSet<>();
        Drawer similarDrawer = new Drawer(bufferBuilder, cameraX, cameraY, cameraZ);
        similarDrawer.setColor(VisualizeBorders.borderColor(true));
        Drawer dissimilarDrawer = new Drawer(bufferBuilder, cameraX, cameraY, cameraZ);
        dissimilarDrawer.setColor(VisualizeBorders.borderColor(false));
        for (Int3 pos : chunksToRender) {
            CalculatedChunkData data = calculatedChunks.get(pos);
            if (data != null) {
                data.drawSimilarBorders(similarDrawer);
                data.drawDissimilarBorders(dissimilarDrawer);
            } else {
                if (chunkReadyForCalculations(pos, world)) {
                    chunksToQueue.add(pos);
                }
            }
        }
        updateChunkCalculations(chunksToQueue, world);
    }

    private boolean chunkReadyForCalculations(Int3 pos, Level world) {
        ChunkPos chunkPos = new ChunkPos(pos.getX(), pos.getZ());
        if (!loadedChunks.contains(chunkPos)) {
            return false;
        }
        if (world.getChunk(pos.getX(), pos.getZ()).getStatus() != ChunkStatus.FULL) {
            return false;
        }
        for (Int3 neighbor : Util.getNeighborChunks(pos)) {
            chunkPos = new ChunkPos(neighbor.getX(), neighbor.getZ());
            if (!loadedChunks.contains(chunkPos)) {
                if (neighbor.getY() < minChunkY || neighbor.getY() > maxChunkY) {
                    continue;
                }
                return false;
            }
            if (world.getChunk(neighbor.getX(), neighbor.getZ()).getStatus() != ChunkStatus.FULL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the calculations of chunks.
     */
    private void updateChunkCalculations(HashSet<Int3> chunksToQueueForCalculation, Level world) {
        synchronized (calculatedChunksToAdd) {
            if (!calculatedChunksToAdd.isEmpty()) {
                calculatedChunks.putAll(calculatedChunksToAdd);
                calculatedChunksToAdd.clear();
            }
            chunksToQueueForCalculation.removeAll(chunksQueuedForCalculation);
            if (!chunksToQueueForCalculation.isEmpty()) {
                if (threadPool == null) {
                    threadPool = Executors.newFixedThreadPool(Config.CLIENT.borderCalculationThreads.get());
                }
            }
            for (Int3 pos : chunksToQueueForCalculation) {
                chunksQueuedForCalculation.add(pos);
                threadPool.execute(new ChunkBiomeBorderCalculator(pos, world, this));
            }
        }
    }

    public void worldUnloaded() {
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }

    public static class Drawer {
        private final VertexConsumer builder;
        private int r, g, b, a;
        private final double cameraX, cameraY, cameraZ;

        private Drawer(VertexConsumer builder, double cameraX, double cameraY, double cameraZ) {
            this.builder = builder;
            this.cameraX = cameraX;
            this.cameraY = cameraY;
            this.cameraZ = cameraZ;
        }

        private void setColor(Color color) {
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public void drawSegment(float x, float y, float z) {
            builder.vertex(x - this.cameraX, y - this.cameraY, z - this.cameraZ).color(r, g, b, a).endVertex();
        }
    }
}
