package team.lodestar.lodestone.neoforge;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import team.lodestar.lodestone.internal.EnchantmentIterationAccess;

public final class LodestoneNeoForgeEnchantmentIteration {
    private LodestoneNeoForgeEnchantmentIteration() {
    }

    public static void install() {
        EnchantmentIterationAccess.install(LodestoneNeoForgeEnchantmentIteration::resolve);
    }

    private static ItemEnchantments resolve(ItemStack stack) {
        net.minecraft.core.HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (lookup == null) {
            return stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }
        return stack.getAllEnchantments(lookup);
    }
}
