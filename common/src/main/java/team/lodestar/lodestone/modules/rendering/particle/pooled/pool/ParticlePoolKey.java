package team.lodestar.lodestone.modules.rendering.particle.pooled.pool;

import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleSpec;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.ParticleComponentType;
import team.lodestar.lodestone.registry.client.LodestoneParticleComponents;

import java.util.BitSet;
import java.util.Objects;

public class ParticlePoolKey {
    private final BitSet componentMask;

    public ParticlePoolKey(BitSet componentMask) {
        this.componentMask = (BitSet) Objects.requireNonNull(componentMask, "componentMask").clone();
    }

    public BitSet componentMask() {
        return (BitSet) componentMask.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticlePoolKey that)) return false;
        return componentMask.equals(that.componentMask);
    }

    @Override
    public int hashCode() {
        return componentMask.hashCode();
    }

    @Override
    public String toString() {
        return "ParticlePoolKey[" + componentMask + "]";
    }

    public static ParticlePoolKey fromSpec(ParticleSpec spec) {
        BitSet mask = new BitSet();

        for (ParticleComponentType<?> type : spec.orderedComponentTypes()) {
            int id = LodestoneParticleComponents.getRegistryId(type);
            mask.set(id);
        }

        return new ParticlePoolKey(mask);
    }
}