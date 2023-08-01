package mrp_v2.biomeborderviewer.client;

import mrp_v2.biomeborderviewer.BiomeBorderViewer;
import mrp_v2.biomeborderviewer.client.renderer.debug.VisualizeBorders;
import mrp_v2.biomeborderviewer.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.awt.*;

@Mod.EventBusSubscriber(modid = BiomeBorderViewer.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final ForgeConfigSpec clientSpec;
    public static final Config CLIENT;
    private static final String TRANSLATION_KEY = BiomeBorderViewer.ID + ".config.gui.";
    private static final String BORDER_A_KEY = "border.a";
    private static final String BORDER_B_KEY = "border.b";
    private static final String RED_KEY = "red";
    private static final String GREEN_KEY = "green";
    private static final String BLUE_KEY = "blue";
    private static final String ALPHA_KEY = "alpha";

    static
    {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public final IntValue borderA_R;
    public final IntValue borderA_G;
    public final IntValue borderA_B;
    public final IntValue borderA_A;
    public final IntValue borderB_R;
    public final IntValue borderB_G;
    public final IntValue borderB_B;
    public final IntValue borderB_A;
    public final IntValue horizontalViewRange;
    public final IntValue verticalViewRange;
    public final IntValue borderCalculationThreads;

    Config(ForgeConfigSpec.Builder builder)
    {
        builder.comment(" biome border viewer client settings").push("client");
        final String bAR = Util.join(BORDER_A_KEY, RED_KEY);
        borderA_R = builder.comment(" The red value of the line's color when the 2 biomes have similar temperatures.")
                .translation(TRANSLATION_KEY + bAR).defineInRange(bAR, 0, 0, 255);
        final String bAG = Util.join(BORDER_A_KEY, GREEN_KEY);
        borderA_G = builder.comment(" The green value of the line's color when the 2 biomes have similar temperatures.")
                .translation(TRANSLATION_KEY + bAG).defineInRange(bAG, 255, 0, 255);
        final String bAB = Util.join(BORDER_A_KEY, BLUE_KEY);
        borderA_B = builder.comment(" The blue value of the line's color when the 2 biomes have similar temperatures.")
                .translation(TRANSLATION_KEY + bAB).defineInRange(bAB, 0, 0, 255);
        final String bAA = Util.join(BORDER_A_KEY, ALPHA_KEY);
        borderA_A = builder.comment(
                " The alpha (transparency) value of the line's color when the 2 biomes have similar temperatures.")
                .translation(TRANSLATION_KEY + bAA).defineInRange(bAA, 64, 0, 255);
        final String bBR = Util.join(BORDER_B_KEY, RED_KEY);
        borderB_R =
                builder.comment(" The red value of the line's color when the 2 biomes have non-similar temperatures.")
                        .translation(TRANSLATION_KEY + bBR).defineInRange(bBR, 255, 0, 255);
        final String bBG = Util.join(BORDER_B_KEY, GREEN_KEY);
        borderB_G =
                builder.comment(" The green value of the line's color when the 2 biomes have non-similar temperatures.")
                        .translation(TRANSLATION_KEY + bBG).defineInRange(bBG, 0, 0, 255);
        final String bBB = Util.join(BORDER_B_KEY, BLUE_KEY);
        borderB_B =
                builder.comment(" The blue value of the line's color when the 2 biomes have non-similar temperatures.")
                        .translation(TRANSLATION_KEY + bBB).defineInRange(bBB, 0, 0, 255);
        final String bBA = Util.join(BORDER_B_KEY, ALPHA_KEY);
        borderB_A = builder.comment(
                " The alpha (transparency) value of the line's color when the 2 biomes have non-similar temperatures.")
                .translation(TRANSLATION_KEY + bBA).defineInRange(bBA, 64, 0, 255);
        final String hVR = "horizontalViewRange";
        horizontalViewRange = builder.comment(" The horizontal distance to show biome borders around the player.",
                " Like render distance, but for the biome border.", " High values may impact performance.")
                .translation(TRANSLATION_KEY + hVR).defineInRange(hVR, 3, 0, 32);
        final String vVR = "verticalViewRange";
        verticalViewRange = builder.comment(" The vertical distance to show biome borders above and below the player.",
                " High values may impact performance.").translation(TRANSLATION_KEY + vVR).defineInRange(vVR, 1, 0, 16);
        final String bCT = "borderCalculationThreads";
        borderCalculationThreads = builder.comment(" How many threads to use to calculate the biome borders.",
                " Only change this if you know what you are doing!").translation(TRANSLATION_KEY + bCT)
                .defineInRange(bCT, 1, 1, 16);
        builder.pop();
    }

    public static Color getColorA()
    {
        return new Color(CLIENT.borderA_R.get(), CLIENT.borderA_G.get(), CLIENT.borderA_B.get(),
                CLIENT.borderA_A.get());
    }

    public static Color getColorB()
    {
        return new Color(CLIENT.borderB_R.get(), CLIENT.borderB_G.get(), CLIENT.borderB_B.get(),
                CLIENT.borderB_A.get());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent)
    {
        LogManager.getLogger().debug(BiomeBorderViewer.DISPLAY_NAME + " config just got changed on the file system!");
        VisualizeBorders.loadConfigSettings();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent)
    {
        LogManager.getLogger().debug("Loaded " + BiomeBorderViewer.DISPLAY_NAME + " config file {}",
                configEvent.getConfig().getFileName());
        VisualizeBorders.loadConfigSettings();
    }
}
