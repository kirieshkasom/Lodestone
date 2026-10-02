package team.lodestar.lodestone.modules.toolkit.enchanting;

import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;

import java.util.function.*;

/**
 * @author SammySemicolon
 * A helper class providing common utility methods for enchantment and other effects
 */
public class LodestoneEnchantmentEffectCommonsHelper {

    /**
     * Determines if the attack from the attacker is a charged attack using the default vanilla threshold of 0.9.
     * @param attacker the entity performing the attack
     * @return true if the attack is charged, false otherwise
     */
    public static boolean isChargedAttack(LivingEntity attacker) {
        return isChargedAttack(attacker, 0.9f);
    }

    /**
     * Determines if the attack from the attacker is a charged attack based on the given threshold.
     * @param attacker the entity performing the attack
     * @param threshold the threshold for considering an attack as charged (between 0 and 1, vanilla uses 0.9)
     * @return true if the attack is charged, false otherwise
     */
    public static boolean isChargedAttack(LivingEntity attacker, float threshold) {
        if (attacker instanceof Player player) {
            return !(player.getAttackStrengthScale(0.5F) <= threshold);
        }
        return true;
    }

    /**
     * Disables the shield for the given duration. If the user is a player, it adds a cooldown to the shield item.
     * @param shieldUser the entity using the shield
     * @param stack the shield item
     * @param duration the duration of the cooldown
     * @return true if the shield was successfully disabled, false if it was already on cooldown
     */
    public static boolean disableShield(LivingEntity shieldUser, ItemStack stack, int duration) {
        return disableShield(shieldUser, stack, duration, true);
    }

    /**
     * Disables the shield for the given duration. If the user is a player, it adds a cooldown to the shield item.
     * @param shieldUser the entity using the shield
     * @param stack the shield item
     * @param duration the duration of the cooldown
     * @param swing whether to make the entity swing their arm
     * @return true if the shield was successfully disabled, false if it was already on cooldown
     */
    public static boolean disableShield(LivingEntity shieldUser, ItemStack stack, int duration, boolean swing) {
        if (shieldUser instanceof Player player) {
            Item shieldItem = stack.getItem();
            boolean wasOnCooldown = player.getCooldowns().isOnCooldown(shieldItem);
            player.stopUsingItem();
            if (!player.isCreative() && !wasOnCooldown) {
                player.getCooldowns().addCooldown(shieldItem, duration);
            }
            return !wasOnCooldown;
        }
        if (swing) {
            var hand = shieldUser.getMainHandItem().equals(stack) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            shieldUser.swing(hand, true);
        }
        return true;
    }

    /**
     * Creates a predicate to determine valid attack targets for the given attacker.
     * Seeks to be somewhat fair when used for area of effect damage
     * @param attacker the entity performing the attack
     * @return a predicate that returns true for valid attack targets
     */
    @SuppressWarnings("RedundantIfStatement")
    public static Predicate<LivingEntity> attackPredicate(LivingEntity attacker) {
        return entity -> {
            if (entity.isSpectator()) {
                return false;
            }
            if (entity == attacker) {
                return false;
            }
            if (attacker.isAlliedTo(entity)) {
                return false;
            }
            if (entity instanceof TamableAnimal tamableanimal && tamableanimal.isTame()) {
                return false;
            }
            if (entity instanceof ArmorStand armorstand && armorstand.isMarker()) {
                return false;
            }
            return true;
        };
    }
}