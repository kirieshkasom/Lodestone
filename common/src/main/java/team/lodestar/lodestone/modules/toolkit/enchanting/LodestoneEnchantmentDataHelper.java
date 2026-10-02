package team.lodestar.lodestone.modules.toolkit.enchanting;

import com.mojang.datafixers.util.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.*;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.predicates.*;

import javax.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

/**
 * @author SammySemicolon
 * A helper designed to assist with data pruning and iteration for enchantments from an item based context.
 */
public class LodestoneEnchantmentDataHelper {

    /**
     * A modified version of {@link EnchantmentHelper#runIterationOnItem(ItemStack, EquipmentSlot, LivingEntity, EnchantmentHelper.EnchantmentInSlotVisitor)}
     *
     * @param stack   The enchanted stack to run enchantment logic on
     * @param filter  The enchantment to limit the search to
     * @param visitor The enchantment consumer to run
     */
    public static void runIterationOnItem(ItemStack stack, @Nullable Holder<Enchantment> filter, EnchantmentHelper.EnchantmentVisitor visitor) {
        runIterationOnItem(stack, filter, visitor, () -> false);
    }

    /**
     * A modified version of {@link EnchantmentHelper#runIterationOnItem(ItemStack, EquipmentSlot, LivingEntity, EnchantmentHelper.EnchantmentInSlotVisitor)}
     *
     * @param stack          The enchanted stack to run enchantment logic on
     * @param filter         The enchantment to limit the search to
     * @param visitor        The enchantment consumer to run
     * @param breakCondition A condition which determines if we should keep iterating
     */
    @SuppressWarnings("DataFlowIssue")
    public static void runIterationOnItem(ItemStack stack, @Nullable Holder<Enchantment> filter, EnchantmentHelper.EnchantmentVisitor visitor, BooleanSupplier breakCondition) {
        ItemEnchantments itemenchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        // We aren't NeoForge, but I want to keep this comment here
        // Neo: Respect gameplay-only enchantments when doing iterations
        var lookup = net.neoforged.neoforge.common.CommonHooks.resolveLookup(net.minecraft.core.registries.Registries.ENCHANTMENT);
        if (lookup != null) {
            itemenchantments = stack.getAllEnchantments(lookup);
        }

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            if (filter == null || filter.is(enchantment.getKey())) {
                visitor.accept(enchantment, entry.getIntValue());
                if (breakCondition.getAsBoolean()) {
                    return;
                }
            }
        }
    }



    public static <T> LocatedEnchantmentEffect<T> findSpecialComponent(ItemStack stack, DataComponentType<T> componentType) {
        return findSpecialComponent(stack, null, componentType);
    }

    public static <T> LocatedEnchantmentEffect<T> findSpecialComponent(ItemStack stack, @Nullable Holder<Enchantment> filter, DataComponentType<T> componentType) {
        AtomicReference<LocatedEnchantmentEffect<T>> result = new AtomicReference<>(new LocatedEnchantmentEffect.EmptyEnchantmentEffect<>());
        try {
            LodestoneEnchantmentDataHelper.runIterationOnItem(stack, filter, (enchantment, enchantmentLevel) -> {
                var componentMap = enchantment.value().effects();
                T special = componentMap.get(componentType);
                if (special != null) {
                    result.set(new LocatedEnchantmentEffect<>(special, enchantment, enchantmentLevel));
                }
            });
        } catch (Exception ignored) {
        }
        return result.get();
    }

    /**
     * Finds all {@link LevelBasedValue} instances stored on an object through the use of reflection
     *
     * @param effectOrComponent The enchantment effect or component to sieve through
     * @return All {@link LevelBasedValue} instances stored on an object in order of definition
     */
    public static List<LevelBasedValue> findLevelBasedValues(Object effectOrComponent) {
        var values = new ArrayList<LevelBasedValue>();
        var clazz = effectOrComponent.getClass();

        record Accessor(Class<?> type, Supplier<Object> getter) {
        }

        List<Accessor> accessors = new ArrayList<>();

        if (clazz.isRecord()) {
            for (RecordComponent component : clazz.getRecordComponents()) {
                var accessor = component.getAccessor();
                accessors.add(
                        new Accessor(component.getType(), () -> {
                            try {
                                return accessor.invoke(effectOrComponent);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to access record: " + component.getName(), e);
                            }
                        })
                );
            }
        } else {
            // Enchantment Components generally don't use classes but let's just be safe
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                accessors.add(
                        new Accessor(field.getType(), () -> {
                            try {
                                return field.get(effectOrComponent);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to access field: " + field.getName(), e);
                            }
                        })
                );
            }
        }

        for (Accessor accessor : accessors) {
            Object object = accessor.getter.get();
            if (object instanceof LevelBasedValue value) {
                values.add(value);
            } else if (object instanceof Optional<?> optional) {
                if (optional.isPresent() && optional.get() instanceof LevelBasedValue value) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    /**
     * Sieves through a data map and finds all components, including nested ones, stored inside that match the given condition.
     * We're basically going through a whole tree map of {@link AllOf} effects, lists, and other conditional effects and returning a raw list of pure effects.
     * Order should be deterministic.
     *
     * @param stack     The item to search through for effects.
     * @param map       The enchantment data map to check.
     * @param type      The class type of effect we are looking for
     * @param condition A condition for either the {@link TypedDataComponent} or the component object itself
     * @return All matching enchantment entity effects stored in the data map.
     */
    protected static <T> List<T> getMatchingEffects(ItemStack stack, DataComponentMap map, Class<T> type, Either<Predicate<TypedDataComponent<?>>, Predicate<T>> condition) {
        return getMatchingEffects(stack, stack, map, type, condition);
    }

    /**
     * Sieves through a data map and finds all components, including nested ones, stored inside that match the given condition.
     * We're basically going through a whole tree map of {@link AllOf} effects, lists, and other conditional effects and returning a raw list of pure effects.
     * Order should be deterministic.
     *
     * @param effectHolder     The item to search through for effects.
     * @param comparisonBroker The item to pass into any conditional effect checks.
     * @param map              The enchantment data map to check.
     * @param type             The class type of effect we are looking for
     * @param condition        A condition for either the {@link TypedDataComponent} or the component object itself
     * @return All matching enchantment entity effects stored in the data map.
     */
    protected static <T> List<T> getMatchingEffects(ItemStack effectHolder, ItemStack comparisonBroker, DataComponentMap map, Class<T> type, Either<Predicate<TypedDataComponent<?>>, Predicate<T>> condition) {
        ArrayList<Object> effectObjects = new ArrayList<>();
        ArrayList<T> result = new ArrayList<>();
        for (TypedDataComponent<?> component : map) {
            Object value = component.value();
            if (condition.left().map(e -> e.test(component)).orElse(true)) {
                if (value instanceof Collection<?> effectList) {
                    effectObjects.addAll(effectList);
                } else {
                    effectObjects.add(value);
                }
            }
        }
        for (Object object : effectObjects) {
            var effects = getEffects(effectHolder, comparisonBroker, object, type);
            for (T effect : effects) {
                if (condition.right().map(e -> e.test(effect)).orElse(true)) {
                    result.add(effect);
                }
            }
        }
        return result;
    }

    /**
     * Finds all enchantment effects, going through conditions and lists if any are defined.
     * @param effectHolder     The item to search through for effects.
     * @param comparisonBroker The item to pass into any conditional effect checks.
     * @param object Any entity effect or effect container, be it a conditional effect or an effect list
     * @param type   The class type of effect we are looking for
     * @return All real effects found on an enchantment data component map.
     */
    private static <T> List<T> getEffects(ItemStack effectHolder, ItemStack comparisonBroker, Object object, Class<T> type) {
        ArrayList<T> effects = new ArrayList<>();
        ArrayList<ConditionalEffect<?>> conditionalEffects = new ArrayList<>();
        ArrayList<TargetedConditionalEffect<?>> targetedConditionalEffects = new ArrayList<>();
        if (object instanceof AllOf.EntityEffects all) {
            for (EnchantmentEntityEffect effect : all.effects()) {
                effects.addAll(getEffects(effectHolder, comparisonBroker, effect, type));
            }
        } else if (object instanceof AllOf.LocationBasedEffects all) {
            for (EnchantmentLocationBasedEffect effect : all.effects()) {
                effects.addAll(getEffects(effectHolder, comparisonBroker, effect, type));
            }
        } else if (object instanceof AllOf.ValueEffects all) {
            for (EnchantmentValueEffect effect : all.effects()) {
                effects.addAll(getEffects(effectHolder, comparisonBroker, effect, type));
            }
        }
        if (type.isInstance(object)) {
            effects.add(type.cast(object));
        } else if (object instanceof TargetedConditionalEffect<?> conditionalEffect) {
            targetedConditionalEffects.add(conditionalEffect);
        } else if (object instanceof ConditionalEffect<?> conditionalEffect) {
            conditionalEffects.add(conditionalEffect);
        }
        for (ConditionalEffect<?> conditionalEffect : conditionalEffects) {
            Optional<LootItemCondition> requirements = conditionalEffect.requirements();
            if (requirements.isEmpty() || matches(comparisonBroker, requirements.get())) {
                effects.addAll(getEffects(effectHolder, comparisonBroker, conditionalEffect.effect(), type));
            }
        }
        for (TargetedConditionalEffect<?> conditionalEffect : targetedConditionalEffects) {
            Optional<LootItemCondition> requirements = conditionalEffect.requirements();
            if (requirements.isEmpty() || matches(comparisonBroker, requirements.get())) {
                effects.addAll(getEffects(effectHolder, comparisonBroker, conditionalEffect.effect(), type));
            }
        }
        return effects;
    }

    /**
     * Checks if the given {@link LootItemCondition} matches the given context.
     * Only considers {@link MatchTool} conditions
     *
     * @param condition The condition of the {@link ConditionalEffect} or {@link TargetedConditionalEffect} to check
     * @param item      The item to check against.
     * @return If the effect's condition was passed
     */
    public static boolean matches(ItemStack item, LootItemCondition condition) {
        if (condition instanceof AllOfCondition all) {
            return all.terms.stream().allMatch(t -> innerMatches(item, t));
        }
        if (condition instanceof AnyOfCondition any) {
            return any.terms.stream().anyMatch(t -> innerMatches(item, t));
        }
        return innerMatches(item, condition);
    }

    /**
     * Checks if an enchantment's item condition matches the given item.
     * Only considers conditions that are directly related to the item itself, rather than damage type or target.
     *
     * @param item      The item to check against.
     * @param condition The condition to check.
     * @return If the item passed any MatchTool conditions present
     */
    protected static boolean innerMatches(ItemStack item, LootItemCondition condition) {
        if (condition instanceof InvertedLootItemCondition(LootItemCondition term)) {
            return !innerMatches(item, term);
        }
        if (condition instanceof MatchTool(Optional<ItemPredicate> predicate)) {
            return predicate.isPresent() && predicate.get().test(item);
        }
        //Non MatchTool conditions should always pass.
        return true;
    }

    /**
     * Retrieves the level of a specific enchantment on the given ItemStack.
     * @param level The current level
     * @param key The ResourceKey of the Enchantment
     * @param stack The ItemStack to check
     * @return The enchantment level as an int
     */
    public static int getEnchantmentLevel(Level level, ResourceKey<Enchantment> key, ItemStack stack) {
        HolderGetter<Enchantment> enchantmentLookup = level.registryAccess().asGetterLookup().lookupOrThrow(Registries.ENCHANTMENT);
        if (stack != null) {
            return stack.getEnchantmentLevel(enchantmentLookup.getOrThrow(key));
        }
        return 0;
    }
}