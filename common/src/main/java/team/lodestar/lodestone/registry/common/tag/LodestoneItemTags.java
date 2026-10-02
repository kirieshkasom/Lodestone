package team.lodestar.lodestone.registry.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import team.lodestar.lodestone.*;

public class LodestoneItemTags {

    public static final TagKey<Item> ENCHANTMENT_HOLDER = tag("enchantment_holder");

    public static final TagKey<Item> MELEE_ENCHANTABLE = tag("enchantable/melee");
    public static final TagKey<Item> RANGED_ENCHANTABLE = tag("enchantable/ranged");
    public static final TagKey<Item> WEAPON_ENCHANTABLE = tag("enchantable/weapon");
    public static final TagKey<Item> SHIELD_ENCHANTABLE = tag("enchantable/shield");
    public static final TagKey<Item> AXE_ENCHANTABLE = tag("enchantable/axe");
    public static final TagKey<Item> KNIFE_ENCHANTABLE = tag("enchantable/knife");

    public static final TagKey<Item> FD_KNIVES = tag("farmersdelight:tools/knives");
    public static final TagKey<Item> C_KNIVES = tag("c:tools/knives");

    public static final TagKey<Item> NUGGETS_COPPER = common("nuggets/copper");
    public static final TagKey<Item> INGOTS_COPPER = common("ingots/copper");
    public static final TagKey<Item> NUGGETS_LEAD = common("nuggets/lead");
    public static final TagKey<Item> INGOTS_LEAD = common("ingots/lead");
    public static final TagKey<Item> NUGGETS_SILVER = common("nuggets/silver");
    public static final TagKey<Item> INGOTS_SILVER = common("ingots/silver");
    public static final TagKey<Item> NUGGETS_ALUMINUM = common("nuggets/aluminum");
    public static final TagKey<Item> INGOTS_ALUMINUM = common("ingots/aluminum");
    public static final TagKey<Item> NUGGETS_NICKEL = common("nuggets/nickel");
    public static final TagKey<Item> INGOTS_NICKEL = common("ingots/nickel");
    public static final TagKey<Item> NUGGETS_URANIUM = common("nuggets/uranium");
    public static final TagKey<Item> INGOTS_URANIUM = common("ingots/uranium");
    public static final TagKey<Item> NUGGETS_OSMIUM = common("nuggets/osmium");
    public static final TagKey<Item> INGOTS_OSMIUM = common("ingots/osmium");
    public static final TagKey<Item> NUGGETS_ZINC = common("nuggets/zinc");
    public static final TagKey<Item> INGOTS_ZINC = common("ingots/zinc");
    public static final TagKey<Item> NUGGETS_TIN = common("nuggets/tin");
    public static final TagKey<Item> INGOTS_TIN = common("ingots/tin");
    public static final TagKey<Item> NUGGETS_COBALT = common("nuggets/cobalt");
    public static final TagKey<Item> INGOTS_COBALT = common("ingots/cobalt");

    public static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, path.contains(":") ? ResourceLocation.parse(path) : LodestoneLib.lodestonePath(path));
    }

    public static TagKey<Item> common(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
    }
}