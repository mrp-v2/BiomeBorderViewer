package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

public class Vec3fUtil {
    public static Vector3f Min(Vector3f a, Vector3f b) {
        return new Vector3f(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()), Math.min(a.z(), b.z()));
    }

    public static Vector3f Max(Vector3f a, Vector3f b) {
        return new Vector3f(Math.max(a.x(), b.x()), Math.max(a.y(), b.y()), Math.max(a.z(), b.z()));
    }

    public static boolean AreValuesOnAxisEqual(Vector3f a, Vector3f b, Direction.Axis axis) {
        return GetValueOnAxis(a, axis) == GetValueOnAxis(b, axis);
    }

    public static float GetValueOnAxis(Vector3f a, Direction.Axis axis) {
        return switch (axis) {
            case X -> a.x();
            case Y -> a.y();
            case Z -> a.z();
        };
    }

    public static Vector3f AddOnOtherAxes(Vector3f a, float f, Direction.Axis axis) {
        return switch (axis) {
            case X -> new Vector3f(a.x(), a.y() + f, a.z() + f);
            case Y -> new Vector3f(a.x() + f, a.y(), a.z() + f);
            case Z -> new Vector3f(a.x() + f, a.y() + f, a.z());
        };
    }

    public static Vector3f AddOnAxis(Vector3f a, float f, Direction.Axis axis) {
        return switch (axis) {
            case X -> new Vector3f(a.x() + f, a.y(), a.z());
            case Y -> new Vector3f(a.x(), a.y() + f, a.z());
            case Z -> new Vector3f(a.x(), a.y(), a.z() + f);
        };
    }
}
