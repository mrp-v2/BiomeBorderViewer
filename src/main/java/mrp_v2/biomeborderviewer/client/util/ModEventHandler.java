package mrp_v2.biomeborderviewer.client.util;

import mrp_v2.biomeborderviewer.BiomeBorderViewer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomeBorderViewer.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent
    public static void registerKeybindings(RegisterKeyMappingsEvent event)
    {
        event.register(ObjectHolder.SHOW_BORDERS);
    }
}
