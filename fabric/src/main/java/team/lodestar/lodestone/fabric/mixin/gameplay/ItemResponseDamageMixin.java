package team.lodestar.lodestone.fabric.mixin.gameplay;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import team.lodestar.lodestone.handlers.ItemEventHandler;
import team.lodestar.lodestone.handlers.ItemEventHandler.DamageContext;
import team.lodestar.lodestone.handlers.ItemEventHandler.DamagePhase;

@Mixin(LivingEntity.class)
public abstract class ItemResponseDamageMixin {
    @WrapMethod(method = "hurt")
    private boolean lodestone$incomingDamage(DamageSource source, float amount, Operation<Boolean> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide || entity.isInvulnerableTo(source) || entity.isDeadOrDying()) {
            return original.call(source, amount);
        }
        DamageContext context = new DamageContext(entity, source, DamagePhase.INCOMING, amount);
        ItemEventHandler.triggerHurtResponses(context);
        return !context.isCanceled() && original.call(source, context.getAmount());
    }
}
