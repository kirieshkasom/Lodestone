package team.lodestar.lodestone.systems.rendering.renderfeature;

import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.rendering.device.DeviceRequirementSet;

import java.util.List;
import java.util.function.Supplier;

public class MultiImplementationRenderFeature<T> implements RenderFeature<T> {
    private final ResourceLocation id;
    private final Entry<T>[] implementations;

    public MultiImplementationRenderFeature(ResourceLocation id, Entry<T>[] implementations) {
        this.id = id;
        this.implementations = implementations;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Entry<T>[] getImplementations() {
        return implementations;
    }

    public static class Builder<T> {
        private final ResourceLocation id;
        private List<Entry<T>> implementations;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public static <T> Builder<T> of(ResourceLocation id) {
            return new Builder<>(id);
        }

        public Builder<T> addImplementation(Supplier<T> implementationSupplier, DeviceRequirementSet requirements) {
            this.implementations.add(new Entry<>(implementationSupplier, requirements));
            return this;
        }

        @SuppressWarnings("unchecked")
        public MultiImplementationRenderFeature<T> build() {
            return new MultiImplementationRenderFeature<>(id, implementations.toArray(new Entry[0]));
        }
    }

    public static class Entry<T> {
        private final Supplier<T> implementationSupplier;
        private final DeviceRequirementSet requirements;

        public Entry(Supplier<T> implementationSupplier, DeviceRequirementSet requirements) {
            this.implementationSupplier = implementationSupplier;
            this.requirements = requirements;
        }

        public Supplier<T> getImplementationSupplier() {
            return implementationSupplier;
        }

        public DeviceRequirementSet getRequirements() {
            return requirements;
        }
    }
}
