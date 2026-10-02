package team.lodestar.lodestone.modules.toolkit.inventory.display;

import net.minecraft.util.*;
import net.minecraft.world.item.*;
import team.lodestar.lodestone.modules.toolkit.inventory.*;

public class ItemDisplayDataEntry {
    protected final ItemStack stack;
    protected final long seed;
    protected float scale, oldScale;
    protected float angle, oldAngle;
    protected float distance, oldDistance;
    protected float lift, oldLift;
    protected float itemAngle, oldItemAngle;
    protected int age;

    public ItemDisplayDataEntry(ItemStack stack, long seed) {
        this.stack = stack;
        this.seed = seed;
    }

    public ItemDisplayDataEntry setAngle(float angle) {
        this.angle = angle;
        this.oldAngle = angle;
        return this;
    }

    public ItemDisplayDataEntry setDistance(float distance) {
        this.distance = distance;
        this.oldDistance = distance;
        return this;
    }

    public ItemDisplayDataEntry setLift(float lift) {
        this.lift = lift;
        this.oldLift = lift;
        return this;
    }

    public ItemDisplayDataEntry setItemAngle(float itemAngle) {
        this.itemAngle = itemAngle;
        this.oldItemAngle = itemAngle;
        return this;
    }

    public void tick(ItemStackHandlerItemDisplayData data,
                     float targetAngle, float addedItemAngle, float targetDistance, float targetLift, float targetScale) {
        oldAngle = angle;
        angle = targetAngle;

        oldItemAngle = itemAngle;
        itemAngle += addedItemAngle;

        oldDistance = distance;
        distance = targetDistance;

        oldLift = lift;
        lift = targetLift;

        oldScale = scale;
        scale = targetScale;
        age++;
    }

    public long getSeed() {
        return seed;
    }

    public float getAngle(float partialTicks) {
        return Mth.rotLerp(partialTicks, oldAngle, angle);
    }

    public float getDistance(float partialTicks) {
        return Mth.lerp(partialTicks, oldDistance, distance);
    }

    public float getLift(float partialTicks) {
        return Mth.lerp(partialTicks, oldLift, lift);
    }

    public float getScale(float partialTicks) {
        return Mth.lerp(partialTicks, oldScale, scale);
    }

    public float getItemRotation(float partialTicks) {
        return Mth.rotLerp(partialTicks, oldItemAngle, itemAngle);
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getAge() {
        return age;
    }

}
