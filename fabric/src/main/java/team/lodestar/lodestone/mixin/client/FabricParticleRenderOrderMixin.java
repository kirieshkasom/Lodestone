package team.lodestar.lodestone.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.modules.rendering.particle.standard.render_types.LodestoneWorldParticleRenderType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public abstract class FabricParticleRenderOrderMixin {

    @Shadow
    @Final
    private Map<ParticleRenderType, Queue<Particle>> particles;

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ParticleEngine;RENDER_ORDER:Ljava/util/List;"))
    private List<ParticleRenderType> lodestone$includeParticleRenderTypes(List<ParticleRenderType> vanillaOrder) {
        List<ParticleRenderType> order = new ArrayList<>(vanillaOrder);
        for (Map.Entry<ParticleRenderType, Queue<Particle>> entry : particles.entrySet()) {
            ParticleRenderType type = entry.getKey();
            if (type instanceof LodestoneWorldParticleRenderType && !entry.getValue().isEmpty() && !order.contains(type)) {
                order.add(type);
            }
        }
        return order;
    }
}
