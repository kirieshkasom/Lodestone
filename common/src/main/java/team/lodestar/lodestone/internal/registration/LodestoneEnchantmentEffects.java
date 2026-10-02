package team.lodestar.lodestone.internal.registration;

import team.lodestar.lodestone.internal.LodestoneCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import team.lodestar.lodestone.modules.toolkit.enchanting.LodestoneSlotBasedEnchantmentAttributeEffect;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LodestoneEnchantmentEffects {

    public static final RegistryEntry<DataComponentType<List<LodestoneSlotBasedEnchantmentAttributeEffect>>> SLOT_BOUND_ATTRIBUTES =
            new RegistryEntry<>(LodestoneCommon.lodestonePath("slot_bound_attributes"), () ->
                    DataComponentType.<List<LodestoneSlotBasedEnchantmentAttributeEffect>>builder()
                            .persistent(LodestoneSlotBasedEnchantmentAttributeEffect.CODEC.codec().listOf())
                            .build()
            );

    public static final LootContextParamSet ENCHANTED_ENTITY = LootContextParamSets.register(
            "lodestone_enchanted_entity",
            builder -> builder.required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ENCHANTMENT_LEVEL)
                    .required(LootContextParams.ORIGIN)
                    .optional(LootContextParams.TOOL)
    );
    public static final LootContextParamSet ENCHANTED_DAMAGE = LootContextParamSets.register(
            "lodestone_enchanted_damage",
            builder -> builder.required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ENCHANTMENT_LEVEL)
                    .required(LootContextParams.ORIGIN)
                    .required(LootContextParams.DAMAGE_SOURCE)
                    .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
                    .optional(LootContextParams.ATTACKING_ENTITY)
                    .optional(LootContextParams.TOOL)
    );

    public static void register(RegistryRegistrar<DataComponentType<?>> registrar) {
        registrar.register(SLOT_BOUND_ATTRIBUTES);
    }

    public static ValueEffectDataComponent valueEffect(RegistryRegistrar<DataComponentType<?>> registry,
                                                       ResourceLocation name) {
        return registry.register(name, () ->
                DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                        .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf())
                        .build()
        )::get;
    }

    public static EntityEffectDataComponent entityEffect(RegistryRegistrar<DataComponentType<?>> registry,
                                                         ResourceLocation name) {
        return registry.register(name, () ->
                DataComponentType.<List<ConditionalEffect<EnchantmentEntityEffect>>>builder()
                        .persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, ENCHANTED_ENTITY).listOf())
                        .build()
        )::get;
    }

    public static TargetedEntityEffectDataComponent targetedEffect(RegistryRegistrar<DataComponentType<?>> registry,
                                                                   ResourceLocation name) {
        return registry.register(name, () ->
                DataComponentType.<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>builder()
                        .persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, ENCHANTED_DAMAGE).listOf())
                        .build()
        )::get;
    }

    public static <T> Supplier<DataComponentType<T>> special(RegistryRegistrar<DataComponentType<?>> registry, ResourceLocation name, Function<DataComponentType.Builder<T>, DataComponentType.Builder<T>> modifier) {
        return registry.register(name, () -> modifier.apply(DataComponentType.builder()).build());
    }


    public interface ValueEffectDataComponent extends Supplier<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> {

    }

    public interface EntityEffectDataComponent extends Supplier<DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>>> {

    }

    public interface TargetedEntityEffectDataComponent extends Supplier<DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> {

    }
}