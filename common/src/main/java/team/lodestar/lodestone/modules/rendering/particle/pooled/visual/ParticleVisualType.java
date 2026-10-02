package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.function.Supplier;

public class ParticleVisualType<T> {
    private final ResourceLocation id;
    private final Supplier<T> configFactory;
    private final ParticleVisualRuntimeFactory<T> runtimeFactory;
    private final RenderLevelStageEvent.Stage renderStage;

    private ParticleVisualType(Builder<T> builder) {
        this.id = builder.id;
        this.configFactory = builder.configFactory;
        this.runtimeFactory = builder.runtimeFactory;
        this.renderStage = builder.renderStage;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Supplier<T> configFactory() {
        return configFactory;
    }

    public ParticleVisualRuntime createRuntime(T config) {
        return runtimeFactory.create(config);
    }

    public RenderLevelStageEvent.Stage renderStage() { // TODO: this
        return renderStage;
    }

    public static <T> Builder<T> builder(ResourceLocation id) {
        return new Builder<>(id);
    }

    public static final class Builder<T> {
        private ResourceLocation id;
        private Supplier<T> configFactory;
        private ParticleVisualRuntimeFactory<T> runtimeFactory;
        private RenderLevelStageEvent.Stage renderStage = RenderLevelStageEvent.Stage.AFTER_PARTICLES;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder<T> configFactory(Supplier<T> configFactory) {
            this.configFactory = configFactory;
            return this;
        }

        public Builder<T> runtimeFactory(ParticleVisualRuntimeFactory<T> runtimeFactory) {
            this.runtimeFactory = runtimeFactory;
            return this;
        }

        public Builder<T> renderStage(RenderLevelStageEvent.Stage renderStage) {
            this.renderStage = renderStage;
            return this;
        }

        public ParticleVisualType<T> build() {
            return new ParticleVisualType<>(this);
        }
    }
}