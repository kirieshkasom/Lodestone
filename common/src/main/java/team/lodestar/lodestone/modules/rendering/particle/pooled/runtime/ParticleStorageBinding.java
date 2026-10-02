package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime;

import team.lodestar.lodestone.modules.rendering.particle.pooled.component.ParticleComponentType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.ParticleComponentStorage;

@SuppressWarnings("rawtypes")
public class ParticleStorageBinding {
    private final ParticleComponentType type;
    private final ParticleComponentStorage storage;

    public ParticleStorageBinding(ParticleComponentType type, ParticleComponentStorage storage) {
        this.type = type;
        this.storage = storage;
    }

    public ParticleComponentType type() {
        return type;
    }

    public ParticleComponentStorage storage() {
        return storage;
    }
}