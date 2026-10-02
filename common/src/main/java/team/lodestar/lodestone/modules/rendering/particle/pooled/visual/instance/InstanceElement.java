package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance;

public record InstanceElement(InstanceWriter writer) {
    public int floatCount() {
        return writer.floatCount();
    }
}