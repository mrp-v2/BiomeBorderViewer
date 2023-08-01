package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mrp_v2.biomeborderviewer.client.Config;
import mrp_v2.biomeborderviewer.client.renderer.debug.VisualizeBorders;
import mrp_v2.biomeborderviewer.util.Util;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BiomeBorderDataCollection
{
    /**
     * Does not need synchronization
     */
    private final HashMap<Int3, CalculatedChunkData> calculatedChunks;
    /**
     * Does not need synchronization
     */
    private final HashSet<Int3> loadedChunks;
    /**
     * Needs synchronization, use this as a lock
     */
    private final HashMap<Int3, CalculatedChunkData> calculatedChunksToAdd;
    /**
     * Needs synchronization, use {@link BiomeBorderDataCollection#calculatedChunksToAdd} as a lock
     */
    private final HashSet<Int3> chunksQueuedForCalculation;
    @Nullable private ExecutorService threadPool;

    public BiomeBorderDataCollection()
    {
        this.calculatedChunks = new HashMap<>();
        this.calculatedChunksToAdd = new HashMap<>();
        this.chunksQueuedForCalculation = new HashSet<>();
        this.loadedChunks = new HashSet<>();
        this.threadPool = null;
    }

    public void chunkLoaded(Int3 pos)
    {
        loadedChunks.add(pos);
    }

    public void chunkUnloaded(Int3 pos)
    {
        loadedChunks.remove(pos);
        calculatedChunks.remove(pos);
    }

    public void chunkCalculated(Int3 pos, CalculatedChunkData data)
    {
        synchronized (calculatedChunksToAdd)
        {
            calculatedChunksToAdd.put(pos, data);
            chunksQueuedForCalculation.remove(pos);
        }
    }

    public boolean areNoChunksLoaded()
    {
        return loadedChunks.isEmpty();
    }

    public void renderBorders(Int3[] chunksToRender, VertexConsumer bufferBuilder, Level world, double cameraX, double cameraY, double cameraZ)
    {
        HashSet<Int3> chunksToQueue = new HashSet<>();
        Drawer similarDrawer = new Drawer(bufferBuilder, cameraX, cameraY, cameraZ);
        similarDrawer.setColor(VisualizeBorders.borderColor(true));
        Drawer dissimilarDrawer = new Drawer(bufferBuilder, cameraX, cameraY, cameraZ);
        dissimilarDrawer.setColor(VisualizeBorders.borderColor(false));
        for (Int3 pos : chunksToRender)
        {
            CalculatedChunkData data = calculatedChunks.get(pos);
            if (data != null)
            {
                data.drawSimilarBorders(similarDrawer);
                data.drawDissimilarBorders(dissimilarDrawer);
            } else
            {
                if (chunkReadyForCalculations(pos, world))
                {
                    chunksToQueue.add(pos);
                }
            }
        }
        updateChunkCalculations(chunksToQueue, world);
    }

    private boolean chunkReadyForCalculations(Int3 pos, Level world)
    {
        if (!loadedChunks.contains(pos))
        {
            return false;
        }
        if (world.getChunk(pos.getX(), pos.getZ()).getStatus() != ChunkStatus.FULL)
        {
            return false;
        }
        for (Int3 neighbor : Util.getNeighborChunks(pos))
        {
            if (!loadedChunks.contains(neighbor))
            {
                if (neighbor.getY() < 0 || neighbor.getY() > 15)
                {
                    continue;
                }
                return false;
            }
            if (world.getChunk(neighbor.getX(), neighbor.getZ()).getStatus() != ChunkStatus.FULL)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the calculations of chunks.
     */
    private void updateChunkCalculations(HashSet<Int3> chunksToQueueForCalculation, Level world)
    {
        synchronized (calculatedChunksToAdd)
        {
            if (!calculatedChunksToAdd.isEmpty())
            {
                calculatedChunks.putAll(calculatedChunksToAdd);
                calculatedChunksToAdd.clear();
            }
            chunksToQueueForCalculation.removeAll(chunksQueuedForCalculation);
            if (!chunksToQueueForCalculation.isEmpty())
            {
                if (threadPool == null)
                {
                    threadPool = Executors.newFixedThreadPool(Config.CLIENT.borderCalculationThreads.get());
                }
            }
            for (Int3 pos : chunksToQueueForCalculation)
            {
                chunksQueuedForCalculation.add(pos);
                threadPool.execute(new ChunkBiomeBorderCalculator(pos, world, this));
            }
        }
    }

    public void worldUnloaded()
    {
        if (threadPool != null)
        {
            threadPool.shutdownNow();
        }
    }

    public static class Drawer
    {
        private final VertexConsumer builder;
        private int r, g, b, a;
        private final double cameraX, cameraY, cameraZ;

        private Drawer(VertexConsumer builder, double cameraX, double cameraY, double cameraZ)
        {
            this.builder = builder;
            this.cameraX = cameraX;
            this.cameraY = cameraY;
            this.cameraZ = cameraZ;
        }

        private void setColor(Color color)
        {
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public void drawSegment(float x, float y, float z)
        {
            builder.vertex(x - this.cameraX, y - this.cameraY, z - this.cameraZ).color(r, g, b, a).endVertex();
        }
    }
}
