package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boids;

import team.lodestar.lodestone.modules.rendering.particle.pooled.component.PreUpdateComponent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.ParticleComponentStorage;

import java.util.Arrays;

public class BoidsStorage implements ParticleComponentStorage<BoidsConfig>, PreUpdateComponent {
    private final float[] perceptionRadiusSq;
    private final float[] separationWeight;
    private final float[] alignmentWeight;
    private final float[] cohesionWeight;
    private final float[] maxSpeed;
    private final float[] maxForce;

    private final double[] ax, ay, az;

    private static final int TABLE_SIZE = 8191;
    private static final double CELL_SIZE = 4.0;

    private final int[] cellStart = new int[TABLE_SIZE];
    private final int[] nextParticle;

    public BoidsStorage(int capacity) {
        this.perceptionRadiusSq = new float[capacity];
        this.separationWeight = new float[capacity];
        this.alignmentWeight = new float[capacity];
        this.cohesionWeight = new float[capacity];
        this.maxSpeed = new float[capacity];
        this.maxForce = new float[capacity];

        this.ax = new double[capacity];
        this.ay = new double[capacity];
        this.az = new double[capacity];

        this.nextParticle = new int[capacity];
    }

    private int hash(int cx, int cy, int cz) {
        int h = (cx * 73856093) ^ (cy * 19349663) ^ (cz * 83492791);
        h = h % TABLE_SIZE;
        return h < 0 ? h + TABLE_SIZE : h;
    }

    private static int fastFloor(double x) {
        int i = (int) x;
        return x < i ? i - 1 : i;
    }

    @Override
    public void preUpdate(int liveCount, float dt, ParticleView particles) {
        if (liveCount == 0) return;

        double[] x = particles.x(), y = particles.y(), z = particles.z();
        double[] vx = particles.vx(), vy = particles.vy(), vz = particles.vz();

        Arrays.fill(cellStart, -1);

        for (int i = 0; i < liveCount; i++) {
            int cx = fastFloor(x[i] / CELL_SIZE);
            int cy = fastFloor(y[i] / CELL_SIZE);
            int cz = fastFloor(z[i] / CELL_SIZE);
            int h = hash(cx, cy, cz);

            nextParticle[i] = cellStart[h];
            cellStart[h] = i;
        }

        for (int i = 0; i < liveCount; i++) {
            double sepX = 0, sepY = 0, sepZ = 0;
            double aliX = 0, aliY = 0, aliZ = 0;
            double cohX = 0, cohY = 0, cohZ = 0;
            int neighborCount = 0;

            float radiusSq = perceptionRadiusSq[i];

            int cx = fastFloor(x[i] / CELL_SIZE);
            int cy = fastFloor(y[i] / CELL_SIZE);
            int cz = fastFloor(z[i] / CELL_SIZE);

            for (int nx = cx - 1; nx <= cx + 1; nx++) {
                for (int ny = cy - 1; ny <= cy + 1; ny++) {
                    for (int nz = cz - 1; nz <= cz + 1; nz++) {
                        int h = hash(nx, ny, nz);
                        int j = cellStart[h];

                        while (j != -1) {
                            if (i != j) {
                                double dx = x[j] - x[i];
                                double dy = y[j] - y[i];
                                double dz = z[j] - z[i];
                                double distSq = dx * dx + dy * dy + dz * dz;

                                if (distSq > 0 && distSq < radiusSq) {
                                    double dist = Math.sqrt(distSq);
                                    sepX -= (dx / dist) / dist;
                                    sepY -= (dy / dist) / dist;
                                    sepZ -= (dz / dist) / dist;
                                    aliX += vx[j]; aliY += vy[j]; aliZ += vz[j];
                                    cohX += x[j];  cohY += y[j];  cohZ += z[j];
                                    neighborCount++;
                                }
                            }
                            j = nextParticle[j];
                        }
                    }
                }
            }

            if (neighborCount > 0) {
                double mSpeed = maxSpeed[i];
                double mForce = maxForce[i];

                double[] sep = {sepX, sepY, sepZ};
                steer(sep, vx[i], vy[i], vz[i], mSpeed, mForce);

                double[] ali = {aliX / neighborCount, aliY / neighborCount, aliZ / neighborCount};
                steer(ali, vx[i], vy[i], vz[i], mSpeed, mForce);

                double[] coh = {(cohX / neighborCount) - x[i], (cohY / neighborCount) - y[i], (cohZ / neighborCount) - z[i]};
                steer(coh, vx[i], vy[i], vz[i], mSpeed, mForce);

                ax[i] = (sep[0] * separationWeight[i]) + (ali[0] * alignmentWeight[i]) + (coh[0] * cohesionWeight[i]);
                ay[i] = (sep[1] * separationWeight[i]) + (ali[1] * alignmentWeight[i]) + (coh[1] * cohesionWeight[i]);
                az[i] = (sep[2] * separationWeight[i]) + (ali[2] * alignmentWeight[i]) + (coh[2] * cohesionWeight[i]);

            } else {
                ax[i] = 0; ay[i] = 0; az[i] = 0;
            }
        }

        for (int i = 0; i < liveCount; i++) {
            vx[i] += ax[i];
            vy[i] += ay[i];
            vz[i] += az[i];

            double mSpeed = maxSpeed[i];
            double speedSq = vx[i] * vx[i] + vy[i] * vy[i] + vz[i] * vz[i];
            if (speedSq > mSpeed * mSpeed) {
                double speed = Math.sqrt(speedSq);
                vx[i] = (vx[i] / speed) * mSpeed;
                vy[i] = (vy[i] / speed) * mSpeed;
                vz[i] = (vz[i] / speed) * mSpeed;
            }
        }
    }

    private void steer(double[] desired, double cvx, double cvy, double cvz, double maxSpeed, double maxForce) {
        setMag(desired, maxSpeed);
        desired[0] -= cvx;
        desired[1] -= cvy;
        desired[2] -= cvz;
        clampMag(desired, maxForce);
    }

    private void setMag(double[] vec, double mag) {
        double currentMag = Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
        if (currentMag > 0) {
            double scale = mag / currentMag;
            vec[0] *= scale;
            vec[1] *= scale;
            vec[2] *= scale;
        }
    }

    private void clampMag(double[] vec, double max) {
        double magSq = vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2];
        if (magSq > max * max) {
            double scale = max / Math.sqrt(magSq);
            vec[0] *= scale;
            vec[1] *= scale;
            vec[2] *= scale;
        }
    }

    @Override
    public void onSpawn(int particleIndex, BoidsConfig config, ParticleSpawnContext spawnContext, ParticleView particles) {
        perceptionRadiusSq[particleIndex] = config.perceptionRadius * config.perceptionRadius;
        separationWeight[particleIndex] = config.separationWeight;
        alignmentWeight[particleIndex] = config.alignmentWeight;
        cohesionWeight[particleIndex] = config.cohesionWeight;
        maxSpeed[particleIndex] = config.maxSpeed;
        maxForce[particleIndex] = config.maxForce;
    }

    @Override
    public void onSwapRemove(int deadIndex, int movedIndex, ParticleView particles) {
        perceptionRadiusSq[deadIndex] = perceptionRadiusSq[movedIndex];
        separationWeight[deadIndex] = separationWeight[movedIndex];
        alignmentWeight[deadIndex] = alignmentWeight[movedIndex];
        cohesionWeight[deadIndex] = cohesionWeight[movedIndex];
        maxSpeed[deadIndex] = maxSpeed[movedIndex];
        maxForce[deadIndex] = maxForce[movedIndex];
    }
}