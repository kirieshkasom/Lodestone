package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.billboard;

import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.*;

import java.util.Objects;

public class BillboardVisualConfig {


    public static final InstanceFormat DEFAULT_FORMAT = new InstanceFormat.Builder()
            .add(StandardInstanceWriters.POSITION)
            .add(StandardInstanceWriters.SCALE)
            .add(StandardInstanceWriters.COLOR)
            .build();

    protected RenderType renderType;
    protected InstanceFormat instanceFormat = DEFAULT_FORMAT;

    public BillboardVisualConfig renderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public BillboardVisualConfig instanceFormat(InstanceFormat instanceFormat) {
        this.instanceFormat = instanceFormat;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillboardVisualConfig that)) return false;
        return Objects.equals(renderType, that.renderType) &&
                Objects.equals(instanceFormat, that.instanceFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderType, instanceFormat);
    }
}