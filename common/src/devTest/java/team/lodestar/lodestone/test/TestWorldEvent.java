package team.lodestar.lodestone.test;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

public final class TestWorldEvent extends WorldEventInstance {
    public Vec3 position = Vec3.ZERO;
    public int age;
    public long lastRenderedTick = -1;
    public int duration = 1200;

    public TestWorldEvent(WorldEventType type) {
        super(type);
    }

    @Override
    public void tick(Level level) {
        age++;
        if (!level.isClientSide) {
            if (age >= duration) {
                end(level);
                setDirty();
            } else if (age % 20 == 0) {
                setDirty();
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);
        tag.putInt("age", age);
        tag.putInt("duration", duration);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        position = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        age = tag.getInt("age");
        duration = tag.getInt("duration");
    }
}
