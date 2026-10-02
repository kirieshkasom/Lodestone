package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance;

import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

import java.nio.FloatBuffer;
import java.util.List;

public class StandardInstanceWriters {

    public static final InstanceWriter POSITION = new PositionWriter();
    public static final InstanceWriter SCALE = new ScaleWriter();
    public static final InstanceWriter SPIN = new SpinWriter();

    public static final InstanceWriter MODEL_MATRIX = new ModelMatrixWriter();
    public static final InstanceWriter COLOR = new ColorWriter();
    public static final InstanceWriter AGE = new AgeWriter();
    public static final InstanceWriter LIFETIME = new LifetimeWriter();

    public static class PositionWriter implements InstanceWriter {

        @Override
        public int floatCount() {
            return 3;
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            float dt = 1.0f; // TODO: Not this, make something better
            float reversePt = 1.0f - partialTicks;
            float camX = (float) camera.getPosition().x;
            float camY = (float) camera.getPosition().y;
            float camZ = (float) camera.getPosition().z;

            int[] visualIds = particles.visualIds();
            double[] xArr = particles.x(), yArr = particles.y(), zArr = particles.z();
            double[] vxArr = particles.vx(), vyArr = particles.vy(), vzArr = particles.vz();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                float x = (float) (xArr[i] - (vxArr[i] * dt * reversePt) - camX);
                float y = (float) (yArr[i] - (vyArr[i] * dt * reversePt) - camY);
                float z = (float) (zArr[i] - (vzArr[i] * dt * reversePt) - camZ);

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                buffer.put(bufferIdx, x);
                buffer.put(bufferIdx + 1, y);
                buffer.put(bufferIdx + 2, z);
                written++;
            }
            return written;
        }
    }

    public static class ScaleWriter implements InstanceWriter {

        @Override
        public int floatCount() {
            return 3;
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            int[] visualIds = particles.visualIds();
            float[] xScale = particles.xScale(), yScale = particles.yScale(), zScale = particles.zScale();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                buffer.put(bufferIdx, xScale[i]);
                buffer.put(bufferIdx + 1, yScale[i]);
                buffer.put(bufferIdx + 2, zScale[i]);
                written++;
            }
            return written;
        }
    }

    public static class SpinWriter implements InstanceWriter {

        @Override
        public int floatCount() {
            return 1;
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            int[] visualIds = particles.visualIds();
            float[] spin = particles.spin();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                buffer.put(bufferIdx, spin[i]);
                written++;
            }
            return written;
        }
    }

    public static class ModelMatrixWriter implements InstanceWriter {
        @Override
        public int floatCount() {
            return 16;
        }

        @Override
        public List<InstanceAttribute> attributes() {
            return List.of(
                    InstanceAttribute.of(4, 0, 1),
                    InstanceAttribute.of(4, 4, 1),
                    InstanceAttribute.of(4, 8, 1),
                    InstanceAttribute.of(4, 12, 1)
            );
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            int[] visualIds = particles.visualIds();

            double[] xArr = particles.x(), yArr = particles.y(), zArr = particles.z();
            double[] vxArr = particles.vx(), vyArr = particles.vy(), vzArr = particles.vz();
            float[] xRotArr = particles.xRot(), yRotArr = particles.yRot(), zRotArr = particles.zRot();
            float[] xScaleArr = particles.xScale(), yScaleArr = particles.yScale(), zScaleArr = particles.zScale();

            float reversePt = 1.0f - partialTicks;
            float camX = (float) camera.getPosition().x;
            float camY = (float) camera.getPosition().y;
            float camZ = (float) camera.getPosition().z;

            Matrix4f model = new Matrix4f();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                float x = (float) (xArr[i] - (vxArr[i] * reversePt) - camX);
                float y = (float) (yArr[i] - (vyArr[i] * reversePt) - camY);
                float z = (float) (zArr[i] - (vzArr[i] * reversePt) - camZ);

                float appliedZRot = zRotArr[i];

                model.identity()
                        .translate(x, y, z)
                        .rotateXYZ(xRotArr[i], yRotArr[i], appliedZRot)
                        .scale(xScaleArr[i], yScaleArr[i], zScaleArr[i]);

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                model.get(bufferIdx, buffer);
                written++;
            }
            return written;
        }
    }

    public static class ColorWriter implements InstanceWriter {
        @Override
        public int floatCount() {
            return 4;
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            int[] visualIds = particles.visualIds();
            float[] rArr = particles.r(), gArr = particles.g(), bArr = particles.b(), aArr = particles.a();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                buffer.put(bufferIdx, rArr[i]);
                buffer.put(bufferIdx + 1, gArr[i]);
                buffer.put(bufferIdx + 2, bArr[i]);
                buffer.put(bufferIdx + 3, aArr[i]);
                written++;
            }
            return written;
        }
    }

    public static class AgeWriter implements InstanceWriter {
        @Override
        public int floatCount() {
            return 1;
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            int[] visualIds = particles.visualIds();
            int[] ageArr = particles.age();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                buffer.put(bufferIdx, ageArr[i]);
                written++;
            }
            return written;
        }
    }

    public static class LifetimeWriter implements InstanceWriter {
        @Override
        public int floatCount() {
            return 1;
        }

        @Override
        public int write(ParticleView particles, int liveCount, int targetId, float partialTicks, Camera camera, FloatBuffer buffer, int startInstance, int elementOffset, int strideFloats) {
            int[] visualIds = particles.visualIds();
            int[] lifetimeArr = particles.lifetime();

            int written = 0;
            for (int i = 0; i < liveCount; i++) {
                if (visualIds[i] != targetId) continue;

                int bufferIdx = ((startInstance + written) * strideFloats) + elementOffset;
                buffer.put(bufferIdx, lifetimeArr[i]);
                written++;
            }
            return written;
        }
    }
}