package mrp_v2.biomeborderviewer.client.renderer.debug.util;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

import java.util.Objects;

public abstract class BorderData
{
    private static final float offset = 1f / 0b11111111;
    protected final Vector3f min, max;

    protected BorderData(Vector3f min, Vector3f max)
    {
        this.min = min;
        this.max = max;
    }

    public static BorderData from(Int3 a, Int3 b)
    {
        Int3 min = Int3.min(a, b);
        Int3 max = Int3.max(a, b);
        if (min.getX() != max.getX())
        {
            if (min.getY() != max.getY() || min.getZ() != max.getZ())
            {
                throw new IllegalArgumentException("Incorrect arguments for border data!");
            }
            return new X(new Vector3f(max.getX(), max.getY(), max.getZ()));
        } else if (min.getY() != max.getY())
        {
            if (min.getZ() != max.getZ())
            {
                throw new IllegalArgumentException("Incorrect arguments for border data!");
            }
            return new Y(new Vector3f(max.getX(), max.getY(), max.getZ()));
        } else if (min.getZ() != max.getZ())
        {
            return new Z(new Vector3f(max.getX(), max.getY(), max.getZ()));
        } else
        {
            throw new IllegalArgumentException("Incorrect arguments for border data!");
        }
    }

    /**
     * Assumes borders can be merged, ensure {@link BorderData#canMerge(BorderData)} is true before calling.
     */
    public static BorderData merge(BorderData a, BorderData b)
    {
        return switch (a.getAxis()) {
            case X -> new X(Vec3fUtil.Min(a.min, b.min), Vec3fUtil.Max(a.max, b.max));
            case Y -> new Y(Vec3fUtil.Min(a.min, b.min), Vec3fUtil.Max(a.max, b.max));
            case Z -> new Z(Vec3fUtil.Min(a.min, b.min), Vec3fUtil.Max(a.max, b.max));
        };
    }

    public abstract Direction.Axis getAxis();

    @Override public int hashCode()
    {
        return Objects.hash(min, max);
    }

    @Override public abstract boolean equals(Object o);

    public boolean equals(BorderData o)
    {
        return this.min.equals(o.min) && this.max.equals(o.max);
    }

    public boolean canMergeOnAxis(BorderData other, Direction.Axis mergeAxis)
    {
        if (this.getAxis() == mergeAxis)
        {
            return false;
        }
        if (other.getAxis() == mergeAxis)
        {
            return false;
        }
        if (Vec3fUtil.AreValuesOnAxisEqual(this.min, other.min, mergeAxis))
        {
            return false;
        }
        return canMerge(other);
    }

    public boolean canMerge(BorderData other)
    {
        if (!this.getAxis().equals(other.getAxis()))
        {
            return false;
        }
        if (!Vec3fUtil.AreValuesOnAxisEqual(this.min, other.min, this.getAxis()))
        {
            return false;
        }
        if (!Vec3fUtil.AreValuesOnAxisEqual(this.max, other.max, this.getAxis()))
        {
            return false;
        }
        Direction.Axis otherAx1 = this.getOtherAxes()[0];
        Direction.Axis otherAx2 = this.getOtherAxes()[1];
        if (Vec3fUtil.AreValuesOnAxisEqual(this.min, other.min, otherAx1) && Vec3fUtil.AreValuesOnAxisEqual(this.max, other.max, otherAx1))
        {
            return Vec3fUtil.AreValuesOnAxisEqual(this.min, other.max, otherAx2) ||
                    Vec3fUtil.AreValuesOnAxisEqual(this.max, other.min, otherAx2);
        } else if (Vec3fUtil.AreValuesOnAxisEqual(this.min, other.min, otherAx2) &&
                Vec3fUtil.AreValuesOnAxisEqual(this.max, other.max, otherAx2))
        {
            return Vec3fUtil.AreValuesOnAxisEqual(this.min, other.max, otherAx1) ||
                    Vec3fUtil.AreValuesOnAxisEqual(this.max, other.min, otherAx1);
        }
        return false;
    }

    public abstract Direction.Axis[] getOtherAxes();

    public static class X extends BorderData
    {
        private static final Direction.Axis[] otherAxes = new Direction.Axis[]{Direction.Axis.Y, Direction.Axis.Z};

        protected X(Vector3f float3)
        {
            super(Vec3fUtil.AddOnAxis(float3, -offset, Direction.Axis.X),
                    Vec3fUtil.AddOnOtherAxes(Vec3fUtil.AddOnAxis(float3, offset, Direction.Axis.X), 1.0F, Direction.Axis.X));
        }

        public X(Vector3f min, Vector3f max)
        {
            super(min, max);
        }

        @Override public Direction.Axis getAxis()
        {
            return Direction.Axis.X;
        }

        @Override public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o instanceof X other))
            {
                return false;
            }
            return super.equals(other);
        }

        @Override public Direction.Axis[] getOtherAxes()
        {
            return otherAxes;
        }
    }

    public static class Y extends BorderData
    {
        private static final Direction.Axis[] otherAxes = new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z};

        protected Y(Vector3f float3)
        {
            super(Vec3fUtil.AddOnAxis(float3, -offset, Direction.Axis.Y),
                    Vec3fUtil.AddOnOtherAxes(Vec3fUtil.AddOnAxis(float3, 1.0F, Direction.Axis.Y), offset, Direction.Axis.Y));
        }

        public Y(Vector3f min, Vector3f max)
        {
            super(min, max);
        }

        @Override public Direction.Axis getAxis()
        {
            return Direction.Axis.Y;
        }

        @Override public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o instanceof Y other))
            {
                return false;
            }
            return super.equals(other);
        }

        @Override public Direction.Axis[] getOtherAxes()
        {
            return otherAxes;
        }
    }

    public static class Z extends BorderData
    {
        private static final Direction.Axis[] otherAxes = new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Y};

        protected Z(Vector3f float3)
        {
            super(Vec3fUtil.AddOnAxis(float3, -offset, Direction.Axis.Z),
                    Vec3fUtil.AddOnAxis(Vec3fUtil.AddOnOtherAxes(float3, 1.0F, Direction.Axis.Z), offset, Direction.Axis.Z));
        }

        public Z(Vector3f min, Vector3f max)
        {
            super(min, max);
        }

        @Override public Direction.Axis getAxis()
        {
            return Direction.Axis.Z;
        }

        @Override public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o instanceof Z other))
            {
                return false;
            }
            return super.equals(other);
        }

        @Override public Direction.Axis[] getOtherAxes()
        {
            return otherAxes;
        }
    }
}
