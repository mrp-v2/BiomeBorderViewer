package mrp_v2.biomeborderviewer.client.util;

import mrp_v2.biomeborderviewer.BiomeBorderViewer;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectHolder
{
    public static final KeyMapping SHOW_BORDERS =
            new KeyMapping(BiomeBorderViewer.ID + ".key.showBorders", GLFW.GLFW_KEY_B,
                    BiomeBorderViewer.ID + ".key.categories");
    public static final Map<Direction.Axis, Direction.Axis[]> AXIS_TO_OTHER_AXES_MAP = new LinkedHashMap<>();

    static
    {
        AXIS_TO_OTHER_AXES_MAP.put(Direction.Axis.X, new Direction.Axis[]{Direction.Axis.Y, Direction.Axis.Z});
        AXIS_TO_OTHER_AXES_MAP.put(Direction.Axis.Y, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z});
        AXIS_TO_OTHER_AXES_MAP.put(Direction.Axis.Z, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Y});
    }
}
