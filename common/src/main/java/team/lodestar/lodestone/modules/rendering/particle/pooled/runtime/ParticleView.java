package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime;

public interface ParticleView {

    double[] x();
    double[] y();
    double[] z();

    double[] vx();
    double[] vy();
    double[] vz();

    float[] r();
    float[] g();
    float[] b();
    float[] a();

    float[] spin();

    float[] xRot();
    float[] yRot();
    float[] zRot();

    float[] xScale();
    float[] yScale();
    float[] zScale();

    int[] age();
    int[] lifetime();

    int[] visualIds();
}
