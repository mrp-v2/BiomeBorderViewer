package mrp_v2.biomeborderviewer.client.util;

import mrp_v2.biomeborderviewer.BiomeBorderViewer;
import mrp_v2.biomeborderviewer.client.renderer.BiomeBorderRenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BiomeBorderViewer.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent public static void clientSetup(FMLClientSetupEvent event)
    {
        ClientRegistry.registerKeyBinding(ObjectHolder.SHOW_BORDERS);
        BiomeBorderRenderType.initBiomeBorderRenderType();
    }
}
