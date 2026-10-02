package team.lodestar.lodestone.systems.rendering.wrapper;

import com.mojang.blaze3d.vertex.*;
import org.jetbrains.annotations.*;

/**
 * A {@link VertexConsumer} that forwards all calls to multiple other {@link VertexConsumer}s.
 * <p>Useful for rendering the same geometry to multiple buffers.</p>
 */
public class LodestoneVertexConsumerWrapper implements VertexConsumer {

    protected final VertexConsumer[] wrapped;

    public LodestoneVertexConsumerWrapper(VertexConsumer... wrapped) {
        this.wrapped = wrapped;
    }

    public VertexConsumer[] getWrappedConsumers() {
        return wrapped;
    }

    @Override
    public @NotNull VertexConsumer addVertex(float x, float y, float z) {
        for (VertexConsumer consumer : getWrappedConsumers()) {
            consumer.addVertex(x, y, z);
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
        for (VertexConsumer consumer : getWrappedConsumers()) {
            consumer.setColor(red, green, blue, alpha);
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv(float u, float v) {
        for (VertexConsumer consumer : getWrappedConsumers()) {
            consumer.setUv(u, v);
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv1(int u, int v) {
        for (VertexConsumer consumer : getWrappedConsumers()) {
            consumer.setUv1(u, v);
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv2(int u, int v) {
        for (VertexConsumer consumer : getWrappedConsumers()) {
            consumer.setUv2(u, v);
        }
        return this;
    }

    @Override
    public @NotNull VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        for (VertexConsumer consumer : getWrappedConsumers()) {
            consumer.setNormal(normalX, normalY, normalZ);
        }
        return this;
    }
}
