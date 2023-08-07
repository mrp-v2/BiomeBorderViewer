package mrp_v2.biomeborderviewer.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mrp_v2.biomeborderviewer.client.Config;
import mrp_v2.biomeborderviewer.client.renderer.debug.util.BiomeBorderDataCollection;
import mrp_v2.biomeborderviewer.client.renderer.debug.util.Int3;
import mrp_v2.biomeborderviewer.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.util.UUID;

public class VisualizeBorders {
    private static Color COLOR_A;
    private static Color COLOR_B;
    private static boolean showingBorders;
    private static int horizontalViewRange, verticalViewRange;
    private static BiomeBorderDataCollection biomeBorderData;

    public static Color borderColor(boolean isSimilar) {
        if (isSimilar) {
            return COLOR_A;
        } else {
            return COLOR_B;
        }
    }

    public static void chunkLoad(LevelAccessor world, ChunkPos chunkPos) {
        if (!(world instanceof ClientLevel)) {
            return;
        }
        if (world.getChunk(chunkPos.x, chunkPos.z).getStatus() != ChunkStatus.FULL) {
            return;
        }
        biomeBorderData.chunkLoaded(chunkPos);
    }

    public static void chunkUnload(LevelAccessor world, ChunkPos chunkPos) {
        if (!(world instanceof ClientLevel)) {
            return;
        }
        biomeBorderData.chunkUnloaded(chunkPos);
    }

    public static void bordersKeyPressed() {
        if (biomeBorderData.areNoChunksLoaded()) {
            return;
        }
        showingBorders = !showingBorders;
        LogManager.getLogger().debug("Show Borders hotkey pressed, showingBorders is now " + showingBorders);
        Minecraft.getInstance().player
                .sendMessage(new TextComponent("Showing borders is now " + showingBorders), UUID.randomUUID());
    }

    public static void loadConfigSettings() {
        horizontalViewRange = Config.CLIENT.horizontalViewRange.get();
        verticalViewRange = Config.CLIENT.verticalViewRange.get();
        COLOR_A = Config.getColorA();
        COLOR_B = Config.getColorB();
    }

    public static void renderEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            if (showingBorders) {
                renderBorders(event);
            }
        }
    }

    private static void renderBorders(RenderLevelStageEvent event) {
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.mulPoseMatrix(event.getPoseStack().last().pose());
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getProfiler().push("biome_borders");
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Vec3 cameraPos = event.getCamera().getPosition();
        biomeBorderData.renderBorders(Util.getChunkColumn(horizontalViewRange, verticalViewRange,
                        new Int3((int) Math.floor(cameraPos.x / 16), (int) Math.floor(cameraPos.y / 16), (int) Math.floor(cameraPos.z / 16))),
                bufferBuilder, event.getCamera().getEntity().getLevel(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z);
        tesselator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        Minecraft.getInstance().getProfiler().pop();
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public static void worldUnload(LevelAccessor world) {
        if (!(world instanceof ClientLevel)) {
            return;
        }
        biomeBorderData.worldUnloaded();
    }

    public static void worldLoad(LevelAccessor world) {
        biomeBorderData = new BiomeBorderDataCollection(world.dimensionType());
    }
}
