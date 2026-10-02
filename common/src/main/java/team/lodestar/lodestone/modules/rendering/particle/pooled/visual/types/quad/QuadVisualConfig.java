package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.quad;

import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.*;

import java.util.Objects;

public class QuadVisualConfig {

    public static final InstanceFormat DEFAULT_FORMAT = new InstanceFormat.Builder()
            .add(StandardInstanceWriters.MODEL_MATRIX)
            .add(StandardInstanceWriters.COLOR)
            .build();

    protected RenderType renderType;
    protected InstanceFormat instanceFormat = DEFAULT_FORMAT;

    public QuadVisualConfig renderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public QuadVisualConfig instanceFormat(InstanceFormat instanceFormat) {
        this.instanceFormat = instanceFormat;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuadVisualConfig that)) return false;
        return Objects.equals(renderType, that.renderType) &&
                Objects.equals(instanceFormat, that.instanceFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderType, instanceFormat);
    }
}