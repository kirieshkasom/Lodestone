package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail;

import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceFormat;

import java.util.Objects;

public class TrailVisualConfig {
    protected RenderType renderType;
    protected int maxPoints = 8;
    protected float width = 0.1f;

    public TrailVisualConfig renderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public TrailVisualConfig maxPoints(int maxPoints) {
        this.maxPoints = Math.max(3, maxPoints);
        return this;
    }

    public TrailVisualConfig width(float width) {
        this.width = width;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrailVisualConfig that)) return false;
        return maxPoints == that.maxPoints &&
                Float.compare(that.width, width) == 0 &&
                Objects.equals(renderType, that.renderType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderType, maxPoints, width);
    }
}