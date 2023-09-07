package mrp_v2.biomeborderviewer.client.util;

import mrp_v2.biomeborderviewer.BiomeBorderViewer;
import mrp_v2.biomeborderviewer.client.renderer.debug.VisualizeBorders;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BiomeBorderViewer.ID)
public class ForgeEventHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void chunkLoad(ChunkEvent.Load event) {
        VisualizeBorders.chunkLoad(event.getLevel(), event.getChunk().getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void chunkUnload(ChunkEvent.Unload event) {
        VisualizeBorders.chunkUnload(event.getLevel(), event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void keyPressed(InputEvent.Key event) {
        if (ObjectHolder.SHOW_BORDERS.consumeClick()) {
            VisualizeBorders.bordersKeyPressed();
        }
    }

    @SubscribeEvent
    public static void renderEvent(RenderLevelStageEvent event) {
        VisualizeBorders.renderEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void worldUnload(LevelEvent.Unload event) {
        VisualizeBorders.worldUnload(event.getLevel());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void worldLoad(LevelEvent.Load event) {
        VisualizeBorders.worldLoad(event.getLevel());
    }
}
