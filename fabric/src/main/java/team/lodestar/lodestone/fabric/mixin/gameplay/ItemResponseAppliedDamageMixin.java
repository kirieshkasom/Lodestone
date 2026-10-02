package team.lodestar.lodestone.fabric.mixin.gameplay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.handlers.ItemEventHandler;
import team.lodestar.lodestone.handlers.ItemEventHandler.DamageContext;
import team.lodestar.lodestone.handlers.ItemEventHandler.DamagePhase;

@Mixin({LivingEntity.class, Player.class})
public abstract class ItemResponseAppliedDamageMixin {
    @ModifyExpressionValue(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
    private float lodestone$beforeDamage(float amount, @Local(argsOnly = true) DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        DamageContext context = new DamageContext(entity, source, DamagePhase.BEFORE_DAMAGE, amount);
        ItemEventHandler.triggerHurtResponses(context);
        return context.isCanceled() ? 0 : context.getAmount();
    }

    @WrapMethod(method = "actuallyHurt")
    private void lodestone$afterDamage(DamageSource source, float amount, Operation<Void> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        float health = entity.getHealth();
        original.call(source, amount);
        ItemEventHandler.triggerHurtResponses(new DamageContext(entity, source, DamagePhase.AFTER_DAMAGE, Math.max(0, health - entity.getHealth())));
    }
}
