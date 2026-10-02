package team.lodestar.lodestone.helpers;

import net.minecraft.core.*;
import net.minecraft.util.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.phys.*;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class EntityHelper {
    public static HitResult pick(LivingEntity entity) {
        double d0 = entity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        double d1 = entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
        return pick(entity, d0, d1);
    }

    public static HitResult pick(Entity entity, double blockInteractionRange, double entityInteractionRange) {
        double d0 = Math.max(blockInteractionRange, entityInteractionRange);
        double d1 = Mth.square(d0);
        Vec3 vec3 = entity.getEyePosition();
        HitResult hitresult = entity.pick(d0, 0, false);
        double d2 = hitresult.getLocation().distanceToSqr(vec3);
        if (hitresult.getType() != HitResult.Type.MISS) {
            d1 = d2;
            d0 = Math.sqrt(d2);
        }

        Vec3 vec31 = entity.getViewVector(0);
        Vec3 vec32 = vec3.add(vec31.x * d0, vec31.y * d0, vec31.z * d0);
        float f = 1.0F;
        AABB aabb = entity.getBoundingBox().expandTowards(vec31.scale(d0)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(
                entity, vec3, vec32, aabb, p_234237_ -> !p_234237_.isSpectator() && p_234237_.isPickable(), d1
        );
        return entityhitresult != null && entityhitresult.getLocation().distanceToSqr(vec3) < d2
                ? filterHitResult(entityhitresult, vec3, entityInteractionRange)
                : filterHitResult(hitresult, vec3, blockInteractionRange);
    }

    public static HitResult filterHitResult(HitResult hitResult, Vec3 pos, double blockInteractionRange) {
        Vec3 vec3 = hitResult.getLocation();
        if (!vec3.closerThan(pos, blockInteractionRange)) {
            Vec3 vec31 = hitResult.getLocation();
            Direction direction = Direction.getNearest(vec31.x - pos.x, vec31.y - pos.y, vec31.z - pos.z);
            return BlockHitResult.miss(vec31, direction, BlockPos.containing(vec31));
        } else {
            return hitResult;
        }
    }

    //TODO: make these into a cool existing effect management helper
    public static void amplifyEffect(MobEffectInstance instance, LivingEntity target, int addedAmplifier, int cap) {
        instance.amplifier = Math.max(Math.min(cap, instance.getAmplifier() + addedAmplifier), instance.getAmplifier());
        syncEffect(instance, target);
    }

    public static void amplifyEffect(MobEffectInstance instance, LivingEntity target, int addedAmplifier) {
        instance.amplifier = instance.getAmplifier() + addedAmplifier;
        syncEffect(instance, target);
    }

    public static void extendEffect(MobEffectInstance instance, LivingEntity target, int addedDuration, int cap) {
        instance.duration = Math.max(Math.min(cap, instance.getDuration() + addedDuration), instance.getDuration());
        syncEffect(instance, target);
    }

    public static void extendEffect(MobEffectInstance instance, LivingEntity target, int addedDuration) {
        instance.duration = instance.getDuration() + addedDuration;
        syncEffect(instance, target);
    }

    public static void shortenEffect(MobEffectInstance instance, LivingEntity target, int removedDuration) {
        instance.duration = instance.getDuration() - removedDuration;
        syncEffect(instance, target);
    }

    public static void syncEffect(MobEffectInstance instance, LivingEntity target) {
        target.effectsDirty = true;
        target.onEffectUpdated(instance, true, target);
    }
}