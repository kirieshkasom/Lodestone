package team.lodestar.lodestone.registry.common;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import team.lodestar.lodestone.fabric.FabricRegistryRegistrar;
import team.lodestar.lodestone.internal.registration.LodestoneEnchantmentEffects;
import team.lodestar.lodestone.internal.registration.RegistryRegistrar;
import team.lodestar.lodestone.modules.toolkit.enchanting.LodestoneSlotBasedEnchantmentAttributeEffect;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LodestoneEnchantmentComponents {
    public static final RegistryRegistrar<DataComponentType<?>> ENCHANTMENT_COMPONENTS = new FabricRegistryRegistrar<>(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);
    public static final Supplier<DataComponentType<List<LodestoneSlotBasedEnchantmentAttributeEffect>>> SLOT_BOUND_ATTRIBUTES = LodestoneEnchantmentEffects.SLOT_BOUND_ATTRIBUTES;
    public static final LootContextParamSet ENCHANTED_ENTITY = LodestoneEnchantmentEffects.ENCHANTED_ENTITY;
    public static final LootContextParamSet ENCHANTED_DAMAGE = LodestoneEnchantmentEffects.ENCHANTED_DAMAGE;

    public static LodestoneEnchantmentEffects.ValueEffectDataComponent valueEffect(RegistryRegistrar<DataComponentType<?>> registry, ResourceLocation id) {
        return LodestoneEnchantmentEffects.valueEffect(registry, id);
    }

    public static LodestoneEnchantmentEffects.EntityEffectDataComponent entityEffect(RegistryRegistrar<DataComponentType<?>> registry, ResourceLocation id) {
        return LodestoneEnchantmentEffects.entityEffect(registry, id);
    }

    public static LodestoneEnchantmentEffects.TargetedEntityEffectDataComponent targetedEffect(RegistryRegistrar<DataComponentType<?>> registry, ResourceLocation id) {
        return LodestoneEnchantmentEffects.targetedEffect(registry, id);
    }

    public static <T> Supplier<DataComponentType<T>> special(RegistryRegistrar<DataComponentType<?>> registry, ResourceLocation id, Function<DataComponentType.Builder<T>, DataComponentType.Builder<T>> modifier) {
        return LodestoneEnchantmentEffects.special(registry, id, modifier);
    }
}
