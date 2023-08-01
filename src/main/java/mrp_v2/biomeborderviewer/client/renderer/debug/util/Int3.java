package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class Int3 extends Vec3i
{
    public Int3(int xIn, int yIn, int zIn)
    {
        super(xIn, yIn, zIn);
    }

    public static Int3 min(Int3 a, Int3 b)
    {
        return new Int3(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
    }

    public static Int3 max(Int3 a, Int3 b)
    {
        return new Int3(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
    }

    public Int3 add(int xIn, int yIn, int zIn)
    {
        if (xIn == 0 && yIn == 0 && zIn == 0)
        {
            return this;
        } else
        {
            return new Int3(this.getX() + xIn, this.getY() + yIn, this.getZ() + zIn);
        }
    }

    public BlockPos toBlockPos()
    {
        return new BlockPos(this);
    }
}
