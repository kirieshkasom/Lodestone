package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime;

public class ParticleSpawnContext {
    public double x = 0;
    public double y = 0;
    public double z = 0;

    public double vx = 0;
    public double vy = 0;
    public double vz = 0;

    public float r = 1.0f;
    public float g = 1.0f;
    public float b = 1.0f;
    public float a = 1.0f;

    public float xRot = 0;
    public float yRot = 0;
    public float zRot = 0;

    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;

    public int lifetime = 20;
    public int delay = 0;

    public ParticleSpawnContext position(double[] data) {
        return position(data[0], data[1], data[2]);
    }

    public ParticleSpawnContext position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public ParticleSpawnContext motion(double vx, double vy, double vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        return this;
    }

    public ParticleSpawnContext color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public ParticleSpawnContext rotation(float xRot, float yRot, float zRot) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
        return this;
    }

    public ParticleSpawnContext scale(float xScale, float yScale, float zScale) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
        return this;
    }

    public ParticleSpawnContext lifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public ParticleSpawnContext delay(int delay) {
        this.delay = delay;
        return this;
    }

    public ParticleSpawnContext copy() {
        return new ParticleSpawnContext().position(x, y, z).motion(vx, vy, vz).lifetime(lifetime).delay(delay);
    }
}
