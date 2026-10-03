package team.lodestar.lodestone.test;

import java.util.function.Supplier;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

public final class TestContent {
    public static final String MOD_ID = "lodestone_tests";
    public static Supplier<WorldEventType> EVENT;
    public static Supplier<Item> HELMET;
    public static Supplier<Item> PERSPECTIVE_PROBE;
    public static Supplier<Item> LAYER_PROBE;
    public static Holder<ArmorMaterial> ARMOR_MATERIAL;

    private TestContent() {
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static WorldEventType createEventType() {
        return new WorldEventType(id("beacon"), () -> new TestWorldEvent(EVENT.get()), true, null);
    }

    public static Item createHelmet() {
        return new ArmorItem(ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().durability(200));
    }

    public static ArmorMaterial createArmorMaterial() {
        ArmorMaterial gold = ArmorMaterials.GOLD.value();
        return new ArmorMaterial(gold.defense(), gold.enchantmentValue(), gold.equipSound(), gold.repairIngredient(),
                List.of(new ArmorMaterial.Layer(id("crown"))), gold.toughness(), gold.knockbackResistance());
    }
}
