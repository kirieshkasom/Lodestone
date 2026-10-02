package team.lodestar.lodestone.internal;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Objects;
import java.util.function.Function;

public final class EnchantmentIterationAccess {
    private static volatile Function<ItemStack, ItemEnchantments> resolver = stack -> stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

    private EnchantmentIterationAccess() {
    }

    public static ItemEnchantments getEnchantments(ItemStack stack) {
        return resolver.apply(stack);
    }

    public static void install(Function<ItemStack, ItemEnchantments> resolver) {
        EnchantmentIterationAccess.resolver = Objects.requireNonNull(resolver);
    }
}
