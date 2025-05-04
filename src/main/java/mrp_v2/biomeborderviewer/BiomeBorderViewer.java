package mrp_v2.biomeborderviewer;

import mrp_v2.biomeborderviewer.client.Config;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BiomeBorderViewer.ID)
public class BiomeBorderViewer {
    public static final String ID = "biome" + "border" + "viewer";
    public static final String DISPLAY_NAME = "Biome Border Viewer";

    public BiomeBorderViewer(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        context.registerExtensionPoint(ExtensionPointProvider.class, ExtensionPointProvider::new);
    }

    private record ExtensionPointProvider() implements IExtensionPoint<ExtensionPointProvider> {

    }
}
