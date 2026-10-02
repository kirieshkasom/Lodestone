package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance;

public record InstanceAttribute(int componentCount, int offsetFloats, int divisor) {
    public InstanceAttribute {
        if (componentCount < 1 || componentCount > 4) {
            throw new IllegalArgumentException("Component count must be between 1 and 4");
        }
        if (offsetFloats < 0) {
            throw new IllegalArgumentException("Offset floats must be >= 0");
        }
        if (divisor < 0) {
            throw new IllegalArgumentException("Divisor must be >= 0");
        }
    }

    public static InstanceAttribute of(int componentCount, int offsetFloats, int divisor) {
        return new InstanceAttribute(componentCount, offsetFloats, divisor);
    }
}