package team.lodestar.lodestone.modules.toolkit.item;

import net.minecraft.core.*;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.flag.*;
import net.minecraft.world.food.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;

import java.util.*;

public class LodestoneItemProperties extends Item.Properties {
    public static final Map<ResourceKey<CreativeModeTab>, List<ResourceLocation>> TAB_SORTING = new HashMap<>();

    public final ResourceKey<CreativeModeTab> tab;

    public LodestoneItemProperties(DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        this(tab.getKey());
    }

    public LodestoneItemProperties(ResourceKey<CreativeModeTab> tab) {
        this.tab = tab;
    }

    public LodestoneItemProperties() {
        this.tab = null;
    }

    public static Item.Properties mergeAttributes(Item.Properties properties, ItemAttributeModifiers attributes) {
        DataComponentMap.Builder components = properties.components;
        if (components != null && components.build().has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers existing = components.build().get(DataComponents.ATTRIBUTE_MODIFIERS);
            var builder = ItemAttributeModifiers.builder();
            if (existing != null) {
                for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
                    builder.add(entry.attribute(), entry.modifier(), entry.slot());
                }
            }
            for (ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
            return properties.attributes(builder.build());
        }
        return properties.attributes(attributes);
    }

    @SuppressWarnings("DataFlowIssue")
    public LodestoneItemProperties mergeAttributes(ItemAttributeModifiers attributes) {
        if (components != null && components.build().has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers existing = components.build().get(DataComponents.ATTRIBUTE_MODIFIERS);
            var builder = ItemAttributeModifiers.builder();
            for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
            for (ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
                builder.add(entry.attribute().getDelegate(), entry.modifier(), entry.slot());
            }
            return attributes(builder.build());
        }
        return attributes(attributes);
    }

    @Override
    public LodestoneItemProperties food(FoodProperties food) {
        return (LodestoneItemProperties)  super.food(food);
    }

    @Override
    public LodestoneItemProperties stacksTo(int maxStackSize) {
        return (LodestoneItemProperties)  super.stacksTo(maxStackSize);
    }

    @Override
    public LodestoneItemProperties durability(int maxDamage) {
        return (LodestoneItemProperties)  super.durability(maxDamage);
    }

    @Override
    public LodestoneItemProperties craftRemainder(Item craftingRemainingItem) {
        return (LodestoneItemProperties)  super.craftRemainder(craftingRemainingItem);
    }

    @Override
    public LodestoneItemProperties rarity(Rarity rarity) {
        return (LodestoneItemProperties)  super.rarity(rarity);
    }

    @Override
    public LodestoneItemProperties fireResistant() {
        return (LodestoneItemProperties)  super.fireResistant();
    }

    @Override
    public LodestoneItemProperties jukeboxPlayable(ResourceKey<JukeboxSong> song) {
        return (LodestoneItemProperties)  super.jukeboxPlayable(song);
    }

    @Override
    public LodestoneItemProperties setNoRepair() {
        return (LodestoneItemProperties)  super.setNoRepair();
    }

    @Override
    public LodestoneItemProperties requiredFeatures(FeatureFlag... requiredFeatures) {
        return (LodestoneItemProperties)  super.requiredFeatures(requiredFeatures);
    }

    @Override
    public <T> LodestoneItemProperties component(DataComponentType<T> component, T value) {
        return (LodestoneItemProperties)  super.component(component, value);
    }

    @Override
    public LodestoneItemProperties attributes(ItemAttributeModifiers attributes) {
        return (LodestoneItemProperties)  super.attributes(attributes);
    }

    public static void addToTabSorting(ResourceLocation itemId, Item.Properties properties) {
        if (properties instanceof LodestoneItemProperties lodestoneItemProperties) {
            if (lodestoneItemProperties.tab == null) {
                return;
            }
            TAB_SORTING.computeIfAbsent(lodestoneItemProperties.tab, (key) -> new ArrayList<>()).add(itemId);
        }
    }

    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() instanceof CategorizedCreativeTab) {
            return;
        }
        var tabKey = event.getTabKey();
        if (TAB_SORTING.containsKey(tabKey)) {
            TAB_SORTING.get(tabKey).stream().map(BuiltInRegistries.ITEM::get)
                    .filter(s -> !event.getParentEntries().contains(s.getDefaultInstance()))
                    .forEach(event::accept);
        }
    }
}