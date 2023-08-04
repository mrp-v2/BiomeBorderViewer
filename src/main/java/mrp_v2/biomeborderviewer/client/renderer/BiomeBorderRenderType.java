package mrp_v2.biomeborderviewer.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public abstract class BiomeBorderRenderType extends RenderType {
    private static RenderType BIOME_BORDER = null;

    private BiomeBorderRenderType(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn,
                                  boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static void initBiomeBorderRenderType() {
        BIOME_BORDER = RenderType.create("biome_border", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, RenderType.SMALL_BUFFER_SIZE, false, true,
                CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setOutputState(RenderStateShard.TRANSLUCENT_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE)
                        .createCompositeState(false));
    }

    public static RenderType getBiomeBorder() {
        return BIOME_BORDER;
    }
}
