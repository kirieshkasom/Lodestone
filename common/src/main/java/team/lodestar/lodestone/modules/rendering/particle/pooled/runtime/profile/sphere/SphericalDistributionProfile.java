package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.sphere;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import team.lodestar.lodestone.modules.core.easing.Easing;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.ParticleSpawnProfile;

import java.util.function.*;

public class SphericalDistributionProfile implements ParticleSpawnProfile {

    private final SphereInfo info;

    public static SphericalDistributionProfile insideOf(AABB aabb) {
        return new SphericalDistributionProfile(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public static SphericalDistributionProfile insideOf(BlockPos pos) {
        return new SphericalDistributionProfile(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public static SphericalDistributionProfile insideOf(BlockPos min, BlockPos max) {
        return new SphericalDistributionProfile(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
    }

    public static SphericalDistributionProfile insideOf(Vector3d min, Vector3d max) {
        return new SphericalDistributionProfile(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static SphericalDistributionProfile insideOf(Vec3 min, Vec3 max) {
        return new SphericalDistributionProfile(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static SphericalDistributionProfile centeredOn(BlockPos pos, double size) {
        return new SphericalDistributionProfile(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, size);
    }

    public static SphericalDistributionProfile centeredOn(Vector3d pos, double size) {
        return new SphericalDistributionProfile(pos.x, pos.y, pos.z, size);
    }

    public static SphericalDistributionProfile centeredOn(Vec3 pos, double size) {
        return new SphericalDistributionProfile(pos.x, pos.y, pos.z, size);
    }

    protected SphericalDistributionProfile(double x, double y, double z, double size) {
        this(x - size / 2, y - size / 2, z - size / 2,
                x + size / 2, y + size / 2, z + size / 2);
    }

    protected SphericalDistributionProfile(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.info = new SphereInfo(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public SphericalDistributionProfile modify(Consumer<SphereInfo> modifier) {
        modifier.accept(info);
        return this;
    }

    @Override
    public void apply(ParticleSpawnContext context, int index, int count) {
        context.position(info.pickPosition());
    }
}
