package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.cube;

import net.minecraft.world.phys.AABB;
import team.lodestar.lodestone.modules.core.easing.Easing;

import java.util.Random;

public class CubeInfo {

    public enum PlacementMode {
        DISTRIBUTE,
        SURROUND,
        OUTLINE,
        CORNERS
    }

    private static final Random CUBE_RANDOM = new Random();

    private final double[] min;
    private final double[] max;

    private PlacementMode mode = PlacementMode.DISTRIBUTE;
    private Easing positionEasing = Easing.LINEAR;

    public CubeInfo(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.min = new double[]{xMin, yMin, zMin};
        this.max = new double[]{xMax, yMax, zMax};
    }

    public CubeInfo withWeightedPosition(Easing positionEasing) {
        this.positionEasing = positionEasing;
        return this;
    }

    public CubeInfo mode(PlacementMode mode) {
        this.mode = mode;
        return this;
    }

    public AABB asAABB() {
        return new AABB(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    public double[] pickPosition() {
        double x = positionEasing.asValueDistribution(CUBE_RANDOM.nextFloat(), min[0], max[0]);
        double y = positionEasing.asValueDistribution(CUBE_RANDOM.nextFloat(), min[1], max[1]);
        double z = positionEasing.asValueDistribution(CUBE_RANDOM.nextFloat(), min[2], max[2]);
        double[] values = new double[]{x, y, z};
        switch (mode) {
            case SURROUND -> {
                int axis = CUBE_RANDOM.nextInt(3);
                values[axis] = snapToFace(values[axis], min[axis], max[axis]);
            }
            case OUTLINE -> {
                int axis = CUBE_RANDOM.nextInt(3);
                int secondAxis = (axis + (CUBE_RANDOM.nextBoolean() ? 1 : 2)) % 3;
                values[axis] = snapToFace(values[axis], min[axis], max[axis]);
                values[secondAxis] = snapToFace(values[secondAxis], min[secondAxis], max[secondAxis]);
            }
            case CORNERS -> {
                for (int i = 0; i < 3; i++) {
                    values[i] = snapToFace(values[i], min[i], max[i]);
                }
            }
        }
        return values;
    }

    public double snapToFace(double value, double min, double max) {
        double half = (min+max)/2;
        if (value < half) {
            return min;
        }
        return max;
    }
}
