package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import net.minecraft.world.level.Level;

public class ChunkBiomeBorderCalculator implements Runnable
{
    private final Int3 pos;
    private final Level world;
    private final BiomeBorderDataCollection resultRecipient;

    public ChunkBiomeBorderCalculator(Int3 pos, Level world, BiomeBorderDataCollection resultRecipient)
    {
        this.pos = pos;
        this.world = world;
        this.resultRecipient = resultRecipient;
    }

    public void run()
    {
        CalculatedChunkData data = new CalculatedChunkData(pos, world);
        resultRecipient.chunkCalculated(pos, data);
    }
}
