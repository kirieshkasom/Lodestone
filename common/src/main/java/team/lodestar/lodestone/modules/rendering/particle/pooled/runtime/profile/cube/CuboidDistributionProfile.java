package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import team.lodestar.lodestone.modules.core.easing.Easing;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.ParticleSpawnProfile;

public class CuboidDistributionProfile implements ParticleSpawnProfile {

    private final CubeInfo info;

    public static CuboidDistributionProfile insideOf(AABB aabb) {
        return new CuboidDistributionProfile(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public static CuboidDistributionProfile insideOf(BlockPos pos) {
        return new CuboidDistributionProfile(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public static CuboidDistributionProfile insideOf(BlockPos min, BlockPos max) {
        return new CuboidDistributionProfile(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
    }

    public static CuboidDistributionProfile insideOf(Vector3d min, Vector3d max) {
        return new CuboidDistributionProfile(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static CuboidDistributionProfile insideOf(Vec3 min, Vec3 max) {
        return new CuboidDistributionProfile(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static CuboidDistributionProfile centeredOn(BlockPos pos, double size) {
        return new CuboidDistributionProfile(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, size);
    }

    public static CuboidDistributionProfile centeredOn(Vector3d pos, double size) {
        return new CuboidDistributionProfile(pos.x, pos.y, pos.z, size);
    }

    public static CuboidDistributionProfile centeredOn(Vec3 pos, double size) {
        return new CuboidDistributionProfile(pos.x, pos.y, pos.z, size);
    }

    protected CuboidDistributionProfile(double x, double y, double z, double size) {
        this(x - size / 2, y - size / 2, z - size / 2,
                x + size / 2, y + size / 2, z + size / 2);
    }

    protected CuboidDistributionProfile(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.info = new CubeInfo(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public CuboidDistributionProfile weighed(Easing weight) {
        info.withWeightedPosition(weight);
        return this;
    }

    public CuboidDistributionProfile outline() {
        return mode(CubeInfo.PlacementMode.OUTLINE);
    }

    public CuboidDistributionProfile surround() {
        return mode(CubeInfo.PlacementMode.SURROUND);
    }

    public CuboidDistributionProfile mode(CubeInfo.PlacementMode mode) {
        info.mode(mode);
        return this;
    }


    @Override
    public void apply(ParticleSpawnContext context, int index, int count) {
        context.position(info.pickPosition());
    }
}
