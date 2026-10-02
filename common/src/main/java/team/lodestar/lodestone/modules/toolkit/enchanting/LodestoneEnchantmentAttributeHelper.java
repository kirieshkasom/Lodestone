package team.lodestar.lodestone.modules.toolkit.enchanting;

import com.google.common.collect.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.enchantment.*;
import org.apache.commons.lang3.mutable.*;
import team.lodestar.lodestone.registry.common.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;

import static net.neoforged.neoforge.common.util.AttributeUtil.*;
import static team.lodestar.lodestone.modules.toolkit.enchanting.LodestoneEnchantmentDataHelper.runIterationOnItem;


/**
 * @author SammySemicolon
 * Helper class for calculating attribute values on items with enchantments.
 */
public class LodestoneEnchantmentAttributeHelper {

    /**
     * Gets the value of an attribute on an item based on modifications from enchantments.
     * @param stack The item stack to check
     * @param filter An optional filter to only include attribute modifiers from a specific enchantment
     * @param attribute The attribute to check
     * @param baseValue The base value of the attribute
     * @return The value of the attribute including enchantment modifications
     */
    public static float getAttributeValue(ItemStack stack, @Nullable Holder<Enchantment> filter, Holder<Attribute> attribute, float baseValue) {
        var mutable = new MutableFloat(baseValue);

        runIterationOnItem(stack, filter, (enchantment, level) -> enchantment.value().getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach(effect -> {
            if (effect.attribute().equals(attribute)) {
                var modifier = effect.getModifier(level, EquipmentSlotGroup.ANY);
                mutable.setValue(compute(modifier, mutable.getValue()));
            }
        }));
        runIterationOnItem(stack, filter, (enchantment, level) -> enchantment.value().getEffects(LodestoneEnchantmentComponents.SLOT_BOUND_ATTRIBUTES.get()).forEach(effect -> {
            if (effect.attribute().equals(attribute)) {
                var modifier = effect.getModifier(level, EquipmentSlotGroup.ANY);
                mutable.setValue(compute(modifier, mutable.getValue()));
            }
        }));

        return mutable.getValue();
    }

    /**
     * Returns the raw value of an attribute on an item as it is by vanilla rules.
     * Any influence from enchantments is ignored.
     *
     * @param modifiers The item's modifiers to check
     * @param baseValue The base value for the attribute
     * @param attribute The attribute to check for
     * @return The value representing the attribute without any enchantment influence.
     */
    public static float getBaseValue(ItemAttributeModifiers modifiers, float baseValue, Holder<Attribute> attribute) {
        return compute(modifiers, e -> e.attribute().equals(attribute) && isBaseAttribute(e), baseValue);
    }

    /**
     * Determines if an attribute modifier entry is a base attribute (not from enchantments)
     * @param entry The entry to check
     * @return True if the entry is a base attribute, false if it is from an enchantment
     */
    public static boolean isBaseAttribute(ItemAttributeModifiers.Entry entry) {
        return !entry.modifier().id().getPath().contains("enchantment");
    }

    /**
     * Converts a Multimap of AttributeModifiers into an ItemAttributeModifiers instance
     * @param map The map to convert
     * @return The converted ItemAttributeModifiers
     */
    public static ItemAttributeModifiers asAttributeModifiers(Multimap<Holder<Attribute>, AttributeModifier> map) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : map.entries()) {
            builder.add(entry.getKey(), entry.getValue(), EquipmentSlotGroup.ANY);
        }
        return builder.build();
    }

    /**
     * Computes the final value of an attribute given a set of modifiers and a base value
     * @param modifiers The modifiers to check
     * @param filter A filter to apply to the modifiers
     * @param baseValue The base value of the attribute
     * @return The final computed value of the attribute
     */
    public static float compute(ItemAttributeModifiers modifiers, Predicate<ItemAttributeModifiers.Entry> filter, double baseValue) {
        double value = baseValue;
        var matching = findMatching(modifiers, filter);
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : matching.entries()) {
            AttributeModifier modifier = entry.getValue();
            value = compute(modifier, value);
        }
        return (float) value;
    }

    /**
     * Finds all matching attribute modifiers from an ItemAttributeModifiers instance based on a filter
     * @param modifiers The modifiers to search
     * @param filter The filter to apply to the modifiers
     * @return A Multimap of matching attribute modifiers
     */
    public static Multimap<Holder<Attribute>, AttributeModifier> findMatching(ItemAttributeModifiers modifiers, Predicate<ItemAttributeModifiers.Entry> filter) {
        Multimap<Holder<Attribute>, AttributeModifier> map = sortedMap();
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (filter.test(entry)) {
                map.put(entry.attribute(), entry.modifier());
            }
        }
        return map;
    }

    /**
     * Computes the final value of an attribute given a single modifier and a base value
     * @param modifier The modifier to apply
     * @param baseValue The base value of the attribute
     * @return The final computed value of the attribute
     */
    public static float compute(AttributeModifier modifier, double baseValue) {
        double value = baseValue;

        double d1 = modifier.amount();

        value += switch (modifier.operation()) {
            case ADD_VALUE -> d1;
            case ADD_MULTIPLIED_BASE -> d1 * baseValue;
            case ADD_MULTIPLIED_TOTAL -> d1 * value;
        };

        return (float) value;
    }
}