package team.lodestar.lodestone.modules.toolkit.worldgen;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Collection;

public class MutableBoundingBox {

    protected int minX = Integer.MAX_VALUE;
    protected int minY = Integer.MAX_VALUE;
    protected int minZ = Integer.MAX_VALUE;
    protected int maxX = Integer.MIN_VALUE;
    protected int maxY = Integer.MIN_VALUE;
    protected int maxZ = Integer.MIN_VALUE;

    public MutableBoundingBox encapsulate(Collection<? extends Vec3i> pos) {
        pos.forEach(this::encapsulate);
        return this;
    }

    public MutableBoundingBox encapsulate(Vec3i... positions) {
        for (Vec3i pos : positions) {
            encapsulate(pos);
        }
        return this;
    }

    public MutableBoundingBox encapsulate(Vec3i pos) {
        if (minX > pos.getX()) {
            minX = pos.getX();
        }
        if (minY > pos.getY()) {
            minY = pos.getY();
        }
        if (minZ > pos.getZ()) {
            minZ = pos.getZ();
        }
        if (maxX < pos.getX()) {
            maxX = pos.getX();
        }
        if (maxY < pos.getY()) {
            maxY = pos.getY();
        }
        if (maxZ < pos.getZ()) {
            maxZ = pos.getZ();
        }
        return this;
    }

    public BoundingBox toBoundingBox() {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}