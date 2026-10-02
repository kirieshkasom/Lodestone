package team.lodestar.lodestone.test;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import team.lodestar.lodestone.modules.rendering.particle.standard.builder.WorldParticleBuilder;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.GenericParticleData;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventRenderer;

public final class TestWorldEventRenderer extends WorldEventRenderer<TestWorldEvent> {
    @Override
    public boolean canRender(TestWorldEvent instance) {
        return !instance.discarded;
    }

    @Override
    public void render(TestWorldEvent event, PoseStack poseStack, MultiBufferSource buffers, float partialTicks) {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().level.getGameTime() == event.lastRenderedTick) {
            return;
        }
        event.lastRenderedTick = Minecraft.getInstance().level.getGameTime();
        double angle = event.age * 0.15;
        WorldParticleBuilder.create(LodestoneParticles.STAR_PARTICLE).setLifetime(30).setForceSpawn(true)
                .setScaleData(GenericParticleData.create(0.25f, 0).build())
                .spawn(Minecraft.getInstance().level, event.position.add(Math.cos(angle), Math.sin(angle), 0));
    }
}
