package team.lodestar.lodestone.modules.rendering.particle.standard.world.options;

import net.minecraft.client.particle.*;
import net.minecraft.core.particles.*;
import team.lodestar.lodestone.helpers.*;
import team.lodestar.lodestone.modules.rendering.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.render_types.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.behaviors.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.*;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderLayer;

import java.util.*;
import java.util.function.*;


public class WorldParticleOptions extends SimpleParticleOptions implements ParticleOptions {

    public final ParticleType<?> type;

    public LodestoneParticleBehavior behavior = BillboardParticleBehavior.INSTANCE;
    public ParticleRenderType renderType = LodestoneWorldParticleRenderType.ADDITIVE;
    public LodestoneRenderLayer renderLayer = LodestoneRenderingSystem.DEFERRED_RENDER;

    public final Collection<Consumer<LodestoneWorldParticle>> tickActors = new ArrayList<>();
    public final Collection<Consumer<LodestoneWorldParticle>> spawnActors = new ArrayList<>();
    public final Collection<Consumer<LodestoneWorldParticle>> renderActors = new ArrayList<>();

    public int particleLight = RenderHelper.FULL_BRIGHT;

    public boolean noClip = false;

    public WorldParticleOptions(ParticleType<?> type) {
        this.type = type;
    }

    public WorldParticleOptions(Supplier<? extends LodestoneWorldParticleType> type) {
        this(type.get());
    }

    public WorldParticleOptions setBehavior(LodestoneParticleBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public WorldParticleOptions setBehaviorIfDefault(LodestoneParticleBehavior newBehavior) {
        if (behavior.equals(BillboardParticleBehavior.INSTANCE)) {
            return setBehavior(newBehavior);
        }
        return this;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }
}