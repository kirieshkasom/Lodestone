package team.lodestar.lodestone.mixin.modules.toolkit;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import team.lodestar.lodestone.handlers.ItemEventHandler;
import team.lodestar.lodestone.handlers.ItemEventHandler.DeathContext;

@Mixin({LivingEntity.class, Player.class, ServerPlayer.class})
public abstract class ItemResponseDeathScopeMixin {
    @WrapMethod(method = "die")
    private void lodestone$deathScope(DamageSource source, Operation<Void> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide) {
            original.call(source);
            return;
        }
        ItemEventHandler.enterDeathResponseScope(entity);
        try {
            DeathContext context = ItemEventHandler.dispatchDeathResponses(entity, source);
            if (context == null || !context.isCanceled()) {
                original.call(source);
            }
        } finally {
            ItemEventHandler.exitDeathResponseScope(entity);
        }
    }
}
