package mrp_v2.biomeborderviewer.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mrp_v2.biomeborderviewer.client.Config;
import mrp_v2.biomeborderviewer.client.renderer.BiomeBorderRenderType;
import mrp_v2.biomeborderviewer.client.renderer.debug.util.BiomeBorderDataCollection;
import mrp_v2.biomeborderviewer.client.renderer.debug.util.Int3;
import mrp_v2.biomeborderviewer.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.util.UUID;

public class VisualizeBorders
{
    private static Color COLOR_A;
    private static Color COLOR_B;
    private static boolean showingBorders;
    private static int horizontalViewRange, verticalViewRange;
    private static BiomeBorderDataCollection biomeBorderData = new BiomeBorderDataCollection();

    public static Color borderColor(boolean isSimilar)
    {
        if (isSimilar)
        {
            return COLOR_A;
        } else
        {
            return COLOR_B;
        }
    }

    public static void chunkLoad(IWorld world, ChunkPos chunkPos)
    {
        if (!(world instanceof ClientWorld))
        {
            return;
        }
        if (world.getChunk(chunkPos.x, chunkPos.z).getStatus() != ChunkStatus.FULL)
        {
            return;
        }
        for (int y = 0; y < 16; y++)
        {
            biomeBorderData.chunkLoaded(new Int3(chunkPos.x, y, chunkPos.z));
        }
    }

    public static void chunkUnload(IWorld world, ChunkPos chunkPos)
    {
        if (!(world instanceof ClientWorld))
        {
            return;
        }
        for (int y = 0; y < 16; y++)
        {
            biomeBorderData.chunkUnloaded(new Int3(chunkPos.x, y, chunkPos.z));
        }
    }

    public static void bordersKeyPressed()
    {
        if (biomeBorderData.areNoChunksLoaded())
        {
            return;
        }
        showingBorders = !showingBorders;
        LogManager.getLogger().debug("Show Borders hotkey pressed, showingBorders is now " + showingBorders);
        Minecraft.getInstance().player
                .sendMessage(new StringTextComponent("Showing borders is now " + showingBorders), UUID.randomUUID());
    }

    public static void loadConfigSettings()
    {
        horizontalViewRange = Config.CLIENT.horizontalViewRange.get();
        verticalViewRange = Config.CLIENT.verticalViewRange.get();
        COLOR_A = Config.getColorA();
        COLOR_B = Config.getColorB();
    }

    public static void renderEvent(float partialTicks, MatrixStack matrixStack)
    {
        if (showingBorders)
        {
            renderBorders(partialTicks, matrixStack);
        }
    }

    private static void renderBorders(float partialTicks, MatrixStack stack)
    {
        Minecraft.getInstance().getProfiler().push("biome_borders");
        Entity player = Minecraft.getInstance().getCameraEntity();
        double cameraX = player.xOld + (player.getX() - player.xOld) * (double) partialTicks;
        double cameraY = player.yOld + (player.getY() - player.yOld) * (double) partialTicks +
                player.getEyeHeight(player.getPose());
        double cameraZ = player.zOld + (player.getZ() - player.zOld) * (double) partialTicks;
        Vector3d playerPos = new Vector3d(cameraX, cameraY, cameraZ);
        IVertexBuilder builder = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(BiomeBorderRenderType.getBiomeBorder());
        stack.pushPose();
        stack.translate(-playerPos.x, -playerPos.y, -playerPos.z);
        Matrix4f matrix = stack.last().pose();
        biomeBorderData.renderBorders(Util.getChunkColumn(horizontalViewRange, verticalViewRange,
                new Int3((int) (playerPos.x / 16), (int) (playerPos.y / 16), (int) (playerPos.z / 16))), matrix,
                builder, player.level);
        stack.popPose();
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(BiomeBorderRenderType.getBiomeBorder());
    }

    public static void worldUnload(IWorld world)
    {
        if (!(world instanceof ClientWorld))
        {
            return;
        }
        biomeBorderData.worldUnloaded();
        biomeBorderData = new BiomeBorderDataCollection();
    }
}
