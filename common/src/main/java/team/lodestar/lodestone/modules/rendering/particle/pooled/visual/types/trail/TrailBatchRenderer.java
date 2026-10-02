package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualBatchKey;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualBatchRenderer;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualSubmission;

import java.util.List;

public class TrailBatchRenderer implements ParticleVisualBatchRenderer {
    public static final TrailBatchRenderer INSTANCE = new TrailBatchRenderer();

    private static float[] SPINE_X = new float[256];
    private static float[] SPINE_Y = new float[256];
    private static float[] SPINE_Z = new float[256];
    private static float[] PERP_X = new float[256];
    private static float[] PERP_Y = new float[256];
    private static float[] PERP_Z = new float[256];

    @Override
    public void renderBatch(ParticleVisualBatchKey key, List<ParticleVisualSubmission> submissions, DeltaTracker partialTicks, Matrix4f viewMat, Matrix4f projMat) {
        RenderType renderType = key.renderType();
        renderType.setupRenderState();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);

        boolean hasData = false;
        boolean isFirstTrail = true;

        float lastX = 0;
        float lastY = 0;
        float lastZ = 0;

        int lastR = 255;
        int lastG = 255;
        int lastB = 255;
        int lastA = 0;

        float lastU = 0;
        float lastV = 0;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;
        Vector3f look = camera.getLookVector();

        float pt = partialTicks.getGameTimeDeltaPartialTick(false);

        for (ParticleVisualSubmission submission : submissions) {
            TrailVisualDrawData data = (TrailVisualDrawData) submission.drawData();
            TrailVisualStorage storage = data.storage();
            int liveCount = data.liveCount();
            float halfWidth = data.width() * 0.5f;

            int[] visualIds = data.particles().visualIds();
            int targetId = data.targetVisualId();

            float[] rArr = data.particles().r();
            float[] gArr = data.particles().g();
            float[] bArr = data.particles().b();
            float[] aArr = data.particles().a();

            double[] vxArr = data.particles().vx();
            double[] vyArr = data.particles().vy();
            double[] vzArr = data.particles().vz();

            synchronized (data.particles()) {
                for (int i = 0; i < liveCount; i++) {
                    if (visualIds[i] != targetId) continue;

                    int points = storage.historyCount()[i];
                    if (points < 2) continue;

                    int spineCount = points + 1;

                    if (spineCount > SPINE_X.length) {
                        int newSize = spineCount * 2;
                        SPINE_X = new float[newSize];
                        SPINE_Y = new float[newSize];
                        SPINE_Z = new float[newSize];
                        PERP_X = new float[newSize];
                        PERP_Y = new float[newSize];
                        PERP_Z = new float[newSize];
                    }

                    double[] xHist = storage.xHistory();
                    double[] yHist = storage.yHistory();
                    double[] zHist = storage.zHistory();
                    int baseIdx = i * storage.maxPoints();

                    for (int j = 0; j < spineCount; j++) {
                        if (j < points - 1) {
                            SPINE_X[j] = (float) (lerp(xHist[baseIdx + j], xHist[baseIdx + j + 1], pt) - camX);
                            SPINE_Y[j] = (float) (lerp(yHist[baseIdx + j], yHist[baseIdx + j + 1], pt) - camY);
                            SPINE_Z[j] = (float) (lerp(zHist[baseIdx + j], zHist[baseIdx + j + 1], pt) - camZ);
                        } else {
                            double lastHx = xHist[baseIdx + points - 1];
                            double lastHy = yHist[baseIdx + points - 1];
                            double lastHz = zHist[baseIdx + points - 1];
                            double curX = lastHx + vxArr[i];
                            double curY = lastHy + vyArr[i];
                            double curZ = lastHz + vzArr[i];

                            SPINE_X[j] = (float) (lerp(lastHx, curX, pt) - camX);
                            SPINE_Y[j] = (float) (lerp(lastHy, curY, pt) - camY);
                            SPINE_Z[j] = (float) (lerp(lastHz, curZ, pt) - camZ);
                        }
                    }

                    for (int j = 0; j < spineCount; j++) {
                        float dx, dy, dz;

                        if (j == 0) {
                            dx = SPINE_X[1] - SPINE_X[0];
                            dy = SPINE_Y[1] - SPINE_Y[0];
                            dz = SPINE_Z[1] - SPINE_Z[0];
                        } else if (j == spineCount - 1) {
                            dx = SPINE_X[j] - SPINE_X[j - 1];
                            dy = SPINE_Y[j] - SPINE_Y[j - 1];
                            dz = SPINE_Z[j] - SPINE_Z[j - 1];
                        } else {
                            dx = SPINE_X[j + 1] - SPINE_X[j - 1];
                            dy = SPINE_Y[j + 1] - SPINE_Y[j - 1];
                            dz = SPINE_Z[j + 1] - SPINE_Z[j - 1];
                        }

                        float px = look.y() * dz - look.z() * dy;
                        float py = look.z() * dx - look.x() * dz;
                        float pz = look.x() * dy - look.y() * dx;

                        float len = (float) Math.sqrt(px * px + py * py + pz * pz);
                        if (len > 0) {
                            PERP_X[j] = (px / len) * halfWidth;
                            PERP_Y[j] = (py / len) * halfWidth;
                            PERP_Z[j] = (pz / len) * halfWidth;
                        } else {
                            PERP_X[j] = PERP_Y[j] = PERP_Z[j] = 0;
                        }
                    }

                    int r = (int) (rArr[i] * 255.0F);
                    int g = (int) (gArr[i] * 255.0F);
                    int b = (int) (bArr[i] * 255.0F);
                    float baseAlpha = aArr[i];

                    if (!isFirstTrail) {
                        builder.addVertex(lastX, lastY, lastZ).setUv(lastU, lastV).setColor(lastR, lastG, lastB, 0);
                        builder.addVertex(SPINE_X[0] - PERP_X[0], SPINE_Y[0] - PERP_Y[0], SPINE_Z[0] - PERP_Z[0]).setUv(0, 0).setColor(r, g, b, 0);
                    }
                    isFirstTrail = false;
                    hasData = true;

                    for (int j = 0; j < spineCount; j++) {
                        float u = (float) j / (spineCount - 1);
                        int alpha = (int) (u * baseAlpha * 255.0F);

                        builder.addVertex(SPINE_X[j] - PERP_X[j], SPINE_Y[j] - PERP_Y[j], SPINE_Z[j] - PERP_Z[j])
                                .setUv(u, 0)
                                .setColor(r, g, b, alpha);

                        lastX = SPINE_X[j] + PERP_X[j];
                        lastY = SPINE_Y[j] + PERP_Y[j];
                        lastZ = SPINE_Z[j] + PERP_Z[j];
                        lastU = u;
                        lastV = 1;
                        lastR = r;
                        lastG = g;
                        lastB = b;
                        lastA = alpha;
                        builder.addVertex(lastX, lastY, lastZ).setUv(u, 1).setColor(r, g, b, alpha);
                    }
                }
            }
        }

        if (hasData) {
            BufferUploader.drawWithShader(builder.buildOrThrow());
        }
        renderType.clearRenderState();
    }

    private double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }
}