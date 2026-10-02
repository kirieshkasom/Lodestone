package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.attractor;

import team.lodestar.lodestone.modules.rendering.particle.pooled.component.PreUpdateComponent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.ParticleComponentStorage;

public class AttractorStorage implements ParticleComponentStorage<AttractorConfig>, PreUpdateComponent {

    private final double[] targetX, targetY, targetZ;
    private final float[] pullStrength;
    private final float[] orbitSpeed;
    private final float[] minDistanceSq;

    public AttractorStorage(int capacity) {
        this.targetX = new double[capacity];
        this.targetY = new double[capacity];
        this.targetZ = new double[capacity];
        this.pullStrength = new float[capacity];
        this.orbitSpeed = new float[capacity];
        this.minDistanceSq = new float[capacity];
    }

    @Override
    public void preUpdate(int liveCount, float dt, ParticleView particles) {
        double[] x = particles.x(), y = particles.y(), z = particles.z();
        double[] vx = particles.vx(), vy = particles.vy(), vz = particles.vz();

        for (int i = 0; i < liveCount; i++) {
            double dx = targetX[i] - x[i];
            double dy = targetY[i] - y[i];
            double dz = targetZ[i] - z[i];

            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > minDistanceSq[i]) {
                double dist = Math.sqrt(distSq);

                double dirX = dx / dist;
                double dirY = dy / dist;
                double dirZ = dz / dist;

                float pull = pullStrength[i];
                vx[i] += dirX * pull;
                vy[i] += dirY * pull;
                vz[i] += dirZ * pull;

                float orbit = orbitSpeed[i];
                if (orbit != 0.0f) {
                    double tangentX = -dirZ;
                    double tangentY = 0;
                    double tangentZ = dirX;

                    vx[i] += tangentX * orbit;
                    vy[i] += tangentY * orbit;
                    vz[i] += tangentZ * orbit;
                }
            }
        }
    }

    @Override
    public void onSpawn(int particleIndex, AttractorConfig config, ParticleSpawnContext spawnContext, ParticleView particles) {
        targetX[particleIndex] = config.targetX;
        targetY[particleIndex] = config.targetY;
        targetZ[particleIndex] = config.targetZ;
        pullStrength[particleIndex] = config.pullStrength;
        orbitSpeed[particleIndex] = config.orbitSpeed;
        minDistanceSq[particleIndex] = config.minDistance * config.minDistance;
    }

    @Override
    public void onSwapRemove(int deadIndex, int movedIndex, ParticleView particles) {
        targetX[deadIndex] = targetX[movedIndex];
        targetY[deadIndex] = targetY[movedIndex];
        targetZ[deadIndex] = targetZ[movedIndex];
        pullStrength[deadIndex] = pullStrength[movedIndex];
        orbitSpeed[deadIndex] = orbitSpeed[movedIndex];
        minDistanceSq[deadIndex] = minDistanceSq[movedIndex];
    }
}