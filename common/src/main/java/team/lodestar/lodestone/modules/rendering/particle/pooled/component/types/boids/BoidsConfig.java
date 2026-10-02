package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boids;

import java.util.Objects;

public class BoidsConfig {
    public float perceptionRadius = 3.0f;
    public float separationWeight = 1.5f;
    public float alignmentWeight = 1.0f;
    public float cohesionWeight = 1.0f;
    public float maxSpeed = 0.4f;
    public float maxForce = 0.02f;

    public BoidsConfig perceptionRadius(float radius) {
        this.perceptionRadius = radius;
        return this;
    }

    public BoidsConfig separationWeight(float weight) {
        this.separationWeight = weight;
        return this;
    }

    public BoidsConfig alignmentWeight(float weight) {
        this.alignmentWeight = weight;
        return this;
    }

    public BoidsConfig cohesionWeight(float weight) {
        this.cohesionWeight = weight;
        return this;
    }

    public BoidsConfig maxSpeed(float speed) {
        this.maxSpeed = speed;
        return this;
    }

    public BoidsConfig maxForce(float force) {
        this.maxForce = force;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BoidsConfig that = (BoidsConfig) o;
        return Float.compare(perceptionRadius, that.perceptionRadius) == 0 &&
                Float.compare(separationWeight, that.separationWeight) == 0 &&
                Float.compare(alignmentWeight, that.alignmentWeight) == 0 &&
                Float.compare(cohesionWeight, that.cohesionWeight) == 0 &&
                Float.compare(maxSpeed, that.maxSpeed) == 0 &&
                Float.compare(maxForce, that.maxForce) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(perceptionRadius, separationWeight, alignmentWeight, cohesionWeight, maxSpeed, maxForce);
    }
}