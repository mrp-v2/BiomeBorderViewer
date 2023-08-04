package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import com.mojang.math.Vector3f;
import mrp_v2.biomeborderviewer.client.renderer.debug.VisualizeBorders;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;

public class CalculatedChunkData {
    private final Vector3f[] similarXMins, similarYMins, similarZMins, similarXMaxs, similarYMaxs, similarZMaxs,
            dissimilarXMins, dissimilarYMins, dissimilarZMins, dissimilarXMaxs, dissimilarYMaxs, dissimilarZMaxs;

    public CalculatedChunkData(Int3 pos, Level world) {
        int xOrigin = pos.getX() * 16, yOrigin = pos.getY() * 16, zOrigin = pos.getZ() * 16;
        int x, z, y;
        Int3 mainPos;
        Biome mainBiome, neighborBiome;
        Int3[] neighbors = new Int3[6];
        ArrayList<BorderData> dissimilarBorders = new ArrayList<>();
        ArrayList<BorderData> similarBorders = new ArrayList<>();
        boolean similar;
        for (y = yOrigin; y < yOrigin + 16; y++) {
            for (x = xOrigin; x < xOrigin + 16; x++) {
                for (z = zOrigin; z < zOrigin + 16; z += 2) {
                    if (z == zOrigin && Math.abs((xOrigin + x) % 2) == (y % 2)) {
                        z++;
                    }
                    mainPos = new Int3(x, y, z);
                    mainBiome = world.getBiome(mainPos.toBlockPos()).value();
                    neighbors[0] = mainPos.add(0, 1, 0);
                    neighbors[1] = mainPos.add(0, -1, 0);
                    neighbors[2] = mainPos.add(1, 0, 0);
                    neighbors[3] = mainPos.add(-1, 0, 0);
                    neighbors[4] = mainPos.add(0, 0, 1);
                    neighbors[5] = mainPos.add(0, 0, -1);
                    for (Int3 neighborPos : neighbors) {
                        if (neighborPos.getY() < VisualizeBorders.MIN_BLOCK_Y || neighborPos.getY() > VisualizeBorders.MAX_BLOCK_Y) {
                            continue;
                        }
                        neighborBiome = world.getBiome(neighborPos.toBlockPos()).value();
                        if (!neighborBiome.equals(mainBiome)) {
                            similar = Math.abs(mainBiome.getBaseTemperature() - neighborBiome.getBaseTemperature()) <
                                    0.1f;
                            if (similar) {
                                similarBorders.add(BorderData.from(mainPos, neighborPos));
                            } else {
                                dissimilarBorders.add(BorderData.from(mainPos, neighborPos));
                            }
                        }
                    }
                }
            }
        }
        simplifyBorders(similarBorders);
        simplifyBorders(dissimilarBorders);
        ArrayList<Vector3f> similarXMins = new ArrayList<>(), similarYMins = new ArrayList<>(), similarZMins =
                new ArrayList<>(), similarXMaxs = new ArrayList<>(), similarYMaxs = new ArrayList<>(), similarZMaxs =
                new ArrayList<>(), dissimilarXMins = new ArrayList<>(), dissimilarYMins = new ArrayList<>(),
                dissimilarZMins = new ArrayList<>(), dissimilarXMaxs = new ArrayList<>(), dissimilarYMaxs =
                new ArrayList<>(), dissimilarZMaxs = new ArrayList<>();
        for (BorderData border : similarBorders) {
            switch (border.getAxis()) {
                case X -> {
                    similarXMins.add(border.min);
                    similarXMaxs.add(border.max);
                }
                case Y -> {
                    similarYMins.add(border.min);
                    similarYMaxs.add(border.max);
                }
                case Z -> {
                    similarZMins.add(border.min);
                    similarZMaxs.add(border.max);
                }
            }
        }
        for (BorderData border : dissimilarBorders) {
            switch (border.getAxis()) {
                case X -> {
                    dissimilarXMins.add(border.min);
                    dissimilarXMaxs.add(border.max);
                }
                case Y -> {
                    dissimilarYMins.add(border.min);
                    dissimilarYMaxs.add(border.max);
                }
                case Z -> {
                    dissimilarZMins.add(border.min);
                    dissimilarZMaxs.add(border.max);
                }
            }
        }
        this.similarXMins = similarXMins.toArray(new Vector3f[0]);
        this.similarXMaxs = similarXMaxs.toArray(new Vector3f[0]);
        this.similarYMins = similarYMins.toArray(new Vector3f[0]);
        this.similarYMaxs = similarYMaxs.toArray(new Vector3f[0]);
        this.similarZMins = similarZMins.toArray(new Vector3f[0]);
        this.similarZMaxs = similarZMaxs.toArray(new Vector3f[0]);
        this.dissimilarXMins = dissimilarXMins.toArray(new Vector3f[0]);
        this.dissimilarXMaxs = dissimilarXMaxs.toArray(new Vector3f[0]);
        this.dissimilarYMins = dissimilarYMins.toArray(new Vector3f[0]);
        this.dissimilarYMaxs = dissimilarYMaxs.toArray(new Vector3f[0]);
        this.dissimilarZMins = dissimilarZMins.toArray(new Vector3f[0]);
        this.dissimilarZMaxs = dissimilarZMaxs.toArray(new Vector3f[0]);
    }

    private static void combineVerticalBorders(ArrayList<BorderData> borders) {
        boolean didSomething = false;
        BorderData borderA, borderB;
        Loop1:
        for (int i1 = 0; i1 < borders.size() - 1; i1++) {
            borderA = borders.get(i1);
            for (int i2 = i1 + 1; i2 < borders.size(); i2++) {
                borderB = borders.get(i2);
                if (borderA.canMergeOnAxis(borderB, Direction.Axis.Y)) {
                    borders.remove(i2);
                    borders.remove(i1);
                    borders.add(i1, BorderData.merge(borderA, borderB));
                    didSomething = true;
                    continue Loop1;
                }
            }
        }
        if (didSomething) {
            combineVerticalBorders(borders);
        }
    }

    private static void simplifyBorders(ArrayList<BorderData> borders) {
        combineVerticalBorders(borders);
        boolean didSomething = false;
        BorderData borderA, borderB;
        Loop1:
        for (int i1 = 0; i1 < borders.size() - 1; i1++) {
            borderA = borders.get(i1);
            for (int i2 = i1 + 1; i2 < borders.size(); i2++) {
                borderB = borders.get(i2);
                if (borderA.canMerge(borderB)) {
                    borders.remove(i2);
                    borders.remove(i1);
                    borders.add(i1, BorderData.merge(borderA, borderB));
                    didSomething = true;
                    continue Loop1;
                }
            }
        }
        if (didSomething) {
            simplifyBorders(borders);
        }
    }

    public static void drawX(BiomeBorderDataCollection.Drawer drawer, Vector3f min, Vector3f max) {
        // -x side
        drawer.drawSegment(min.x(), min.y(), min.z());
        drawer.drawSegment(min.x(), min.y(), max.z());
        drawer.drawSegment(min.x(), max.y(), max.z());
        drawer.drawSegment(min.x(), max.y(), min.z());
        // +x side
        drawer.drawSegment(max.x(), min.y(), min.z());
        drawer.drawSegment(max.x(), max.y(), min.z());
        drawer.drawSegment(max.x(), max.y(), max.z());
        drawer.drawSegment(max.x(), min.y(), max.z());
    }

    public static void drawY(BiomeBorderDataCollection.Drawer drawer, Vector3f min, Vector3f max) {
        // -y side
        drawer.drawSegment(min.x(), min.y(), min.z());
        drawer.drawSegment(max.x(), min.y(), min.z());
        drawer.drawSegment(max.x(), min.y(), max.z());
        drawer.drawSegment(min.x(), min.y(), max.z());
        // +y side
        drawer.drawSegment(min.x(), max.y(), min.z());
        drawer.drawSegment(min.x(), max.y(), max.z());
        drawer.drawSegment(max.x(), max.y(), max.z());
        drawer.drawSegment(max.x(), max.y(), min.z());
    }

    public static void drawZ(BiomeBorderDataCollection.Drawer drawer, Vector3f min, Vector3f max) {
        // -z side
        drawer.drawSegment(min.x(), min.y(), min.z());
        drawer.drawSegment(min.x(), max.y(), min.z());
        drawer.drawSegment(max.x(), max.y(), min.z());
        drawer.drawSegment(max.x(), min.y(), min.z());
        // +z side
        drawer.drawSegment(min.x(), min.y(), max.z());
        drawer.drawSegment(max.x(), min.y(), max.z());
        drawer.drawSegment(max.x(), max.y(), max.z());
        drawer.drawSegment(min.x(), max.y(), max.z());
    }

    public void drawSimilarBorders(BiomeBorderDataCollection.Drawer drawer) {
        for (int i = 0; i < similarXMins.length; i++) {
            drawX(drawer, similarXMins[i], similarXMaxs[i]);
        }
        for (int i = 0; i < similarYMins.length; i++) {
            drawY(drawer, similarYMins[i], similarYMaxs[i]);
        }
        for (int i = 0; i < similarZMins.length; i++) {
            drawZ(drawer, similarZMins[i], similarZMaxs[i]);
        }
    }

    public void drawDissimilarBorders(BiomeBorderDataCollection.Drawer drawer) {
        for (int i = 0; i < dissimilarXMins.length; i++) {
            drawX(drawer, dissimilarXMins[i], dissimilarXMaxs[i]);
        }
        for (int i = 0; i < dissimilarYMins.length; i++) {
            drawY(drawer, dissimilarYMins[i], dissimilarYMaxs[i]);
        }
        for (int i = 0; i < dissimilarZMins.length; i++) {
            drawZ(drawer, dissimilarZMins[i], dissimilarZMaxs[i]);
        }
    }
}
