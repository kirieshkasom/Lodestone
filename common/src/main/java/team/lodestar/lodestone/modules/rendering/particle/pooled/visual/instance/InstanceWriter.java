package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance;

import net.minecraft.client.Camera;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

import java.nio.FloatBuffer;
import java.util.List;

public interface InstanceWriter {

    int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats);

    int floatCount();

    default List<InstanceAttribute> attributes() {
        return List.of(InstanceAttribute.of(floatCount(), 0, 1));
    }

    default int attributeCount() {
        return attributes().size();
    }
}