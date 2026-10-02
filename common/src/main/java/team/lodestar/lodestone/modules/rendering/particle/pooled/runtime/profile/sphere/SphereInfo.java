package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.sphere;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import team.lodestar.lodestone.modules.core.easing.Easing;

public class SphereInfo {

    public enum PlacementMode {
        DISTRIBUTE,
        OUTLINE
    }

    protected static final RandomSource SPHERE_RANDOM = RandomSource.create();

    protected final double[] min;
    protected final double[] max;

    protected PlacementMode mode = PlacementMode.DISTRIBUTE;
    protected Easing angleWeighing = Easing.LINEAR;

    protected double minAngle = 0, maxAngle = 6.28;

    public SphereInfo(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.min = new double[]{xMin, yMin, zMin};
        this.max = new double[]{xMax, yMax, zMax};
    }

    public SphereInfo mode(PlacementMode mode) {
        this.mode = mode;
        return this;
    }

    public SphereInfo withWeightedAngle(Easing angleWeighing) {
        this.angleWeighing = angleWeighing;
        return this;
    }

    public SphereInfo clampAngle(double minAngle, double maxAngle) {
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        return this;
    }

    public double[] pickPosition() {
        double angleDelta = SPHERE_RANDOM.nextDouble();
        double angle = angleWeighing.asValueDistribution(
                angleDelta, minAngle, maxAngle
        );

        double pitch = SPHERE_RANDOM.nextDouble() * 6.28 - 3.14;
        return pickPosition(angle, pitch);
    }

    public double[] pickPosition(double angle, double pitch) {
        double cosPitch = Math.cos(pitch);

        double x = Math.cos(angle) * cosPitch;
        double y = Math.sin(pitch);
        double z = Math.sin(angle) * cosPitch;

        x = (x + 1.0) * 0.5;
        y = (y + 1.0) * 0.5;
        z = (z + 1.0) * 0.5;

        x = Mth.lerp(x, min[0], max[0]);
        y = Mth.lerp(y, min[1], max[1]);
        z = Mth.lerp(z, min[2], max[2]);

        if (mode.equals(PlacementMode.DISTRIBUTE)) {
            float delta = SPHERE_RANDOM.nextFloat();
            x = Mth.lerp(delta, (min[0] + max[0])/2f, x);
            y = Mth.lerp(delta, (min[1] + max[1])/2f, y);
            z = Mth.lerp(delta, (min[2] + max[2])/2f, z);
        }

        return new double[]{x, y, z};
    }
}
