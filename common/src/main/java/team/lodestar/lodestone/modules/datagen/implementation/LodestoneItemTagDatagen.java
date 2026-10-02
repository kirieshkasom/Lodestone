package team.lodestar.lodestone.modules.datagen.implementation;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.datagen.providers.tag.LodestoneItemTagsSystem;
import team.lodestar.lodestone.registry.common.tag.LodestoneItemTags;

import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.AXE_ENCHANTABLE;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.C_KNIVES;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.FD_KNIVES;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_ALUMINUM;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_COBALT;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_COPPER;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_LEAD;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_NICKEL;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_OSMIUM;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_SILVER;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_TIN;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_URANIUM;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.INGOTS_ZINC;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.KNIFE_ENCHANTABLE;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.MELEE_ENCHANTABLE;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_ALUMINUM;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_COBALT;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_COPPER;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_LEAD;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_NICKEL;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_OSMIUM;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_SILVER;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_TIN;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_URANIUM;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.NUGGETS_ZINC;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.RANGED_ENCHANTABLE;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.SHIELD_ENCHANTABLE;
import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.WEAPON_ENCHANTABLE;

public final class LodestoneItemTagDatagen extends LodestoneItemTagsSystem {
    public LodestoneItemTagDatagen(PackOutput output) {
        super(output, LodestoneCommon.LODESTONE);
        addTags();
    }

    private void addTags() {
        tag(LodestoneItemTags.ENCHANTMENT_HOLDER).add(Items.BOOK, Items.ENCHANTED_BOOK);

        tag(MELEE_ENCHANTABLE).addTags(ItemTags.SWORD_ENCHANTABLE, AXE_ENCHANTABLE, ItemTags.TRIDENT_ENCHANTABLE, ItemTags.MACE_ENCHANTABLE, KNIFE_ENCHANTABLE);
        tag(RANGED_ENCHANTABLE).addTags(ItemTags.CROSSBOW_ENCHANTABLE, ItemTags.BOW_ENCHANTABLE);
        tag(WEAPON_ENCHANTABLE).addTags(MELEE_ENCHANTABLE, RANGED_ENCHANTABLE);
        tag(SHIELD_ENCHANTABLE).add(Items.SHIELD);
        tag(AXE_ENCHANTABLE).addTags(ItemTags.AXES);
        tag(KNIFE_ENCHANTABLE).addTags(FD_KNIVES, C_KNIVES);

        tag(FD_KNIVES);
        tag(C_KNIVES);

        tag(NUGGETS_COPPER);
        tag(INGOTS_COPPER).add(Items.COPPER_INGOT);
        tag(NUGGETS_LEAD);
        tag(INGOTS_LEAD);
        tag(NUGGETS_SILVER);
        tag(INGOTS_SILVER);
        tag(NUGGETS_ALUMINUM);
        tag(INGOTS_ALUMINUM);
        tag(NUGGETS_NICKEL);
        tag(INGOTS_NICKEL);
        tag(NUGGETS_URANIUM);
        tag(INGOTS_URANIUM);
        tag(NUGGETS_OSMIUM);
        tag(INGOTS_OSMIUM);
        tag(NUGGETS_ZINC);
        tag(INGOTS_ZINC);
        tag(NUGGETS_TIN);
        tag(INGOTS_TIN);
        tag(NUGGETS_COBALT);
        tag(INGOTS_COBALT);
    }
}
