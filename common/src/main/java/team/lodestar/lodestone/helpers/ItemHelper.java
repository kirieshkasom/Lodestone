package team.lodestar.lodestone.helpers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ItemHelper {

    public static ArrayList<ItemStack> copyWithNewCount(List<ItemStack> stacks, int newCount) {
        ArrayList<ItemStack> newStacks = new ArrayList<>();
        for (ItemStack stack : stacks) {
            ItemStack copy = stack.copy();
            copy.setCount(newCount);
            newStacks.add(copy);
        }
        return newStacks;
    }

    public static ItemStack copyWithNewCount(ItemStack stack, int newCount) {
        ItemStack newStack = stack.copy();
        newStack.setCount(newCount);
        return newStack;
    }

    public static <T extends Entity> Entity getClosestEntity(List<T> entities, Vec3 pos) {
        double cachedDistance = -1.0D;
        Entity resultEntity = null;

        for (T entity : entities) {
            double newDistance = entity.distanceToSqr(pos.x, pos.y, pos.z);
            if (cachedDistance == -1.0D || newDistance < cachedDistance) {
                cachedDistance = newDistance;
                resultEntity = entity;
            }

        }
        return resultEntity;
    }

    public static void giveItemToEntity(LivingEntity entity, ItemStack stack) {
        if (entity instanceof Player player) {
            player.getInventory().placeItemBackInInventory(stack);
        } else {
            spawnItemOnEntity(entity, stack);
        }
    }

    public static void quietlyGiveItemToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;
        Level level = player.level();
        if (!stack.isEmpty() && !player.getInventory().add(stack) && !level.isClientSide) {
            spawnItemOnEntity(player, stack);
        }
    }

    public static void spawnItemOnEntity(LivingEntity entity, ItemStack stack) {
        Level level = entity.level();
        ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY() + 0.5, entity.getZ(), stack);
        itemEntity.setPickUpDelay(40);
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
        level.addFreshEntity(itemEntity);
    }
}
