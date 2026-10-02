package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import org.joml.Matrix4f;

import java.util.List;

public interface ParticleVisualBatchRenderer {
    void renderBatch(ParticleVisualBatchKey key, List<ParticleVisualSubmission> submissions, float partialTicks, Matrix4f viewMat, Matrix4f projMat);
}
