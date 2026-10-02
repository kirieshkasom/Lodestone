package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.attractor;

import java.util.Objects;

public class AttractorConfig {
    public double targetX = 0, targetY = 0, targetZ = 0;
    public float pullStrength = 0.05f;
    public float orbitSpeed = 0.0f;
    public float minDistance = 1.0f;

    public AttractorConfig target(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        return this;
    }

    public AttractorConfig pullStrength(float strength) {
        this.pullStrength = strength;
        return this;
    }

    public AttractorConfig orbitSpeed(float speed) {
        this.orbitSpeed = speed;
        return this;
    }

    public AttractorConfig minDistance(float distance) {
        this.minDistance = distance;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AttractorConfig that = (AttractorConfig) o;
        return Double.compare(targetX, that.targetX) == 0 &&
                Double.compare(targetY, that.targetY) == 0 &&
                Double.compare(targetZ, that.targetZ) == 0 &&
                Float.compare(pullStrength, that.pullStrength) == 0 &&
                Float.compare(orbitSpeed, that.orbitSpeed) == 0 &&
                Float.compare(minDistance, that.minDistance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetX, targetY, targetZ, pullStrength, orbitSpeed, minDistance);
    }
}