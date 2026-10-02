package team.lodestar.lodestone.modules.core.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record BlockItemTagKey(TagKey<Item> itemTag, TagKey<Block> blockTag) {

    public BlockItemTagKey(ResourceLocation id) {
        this(TagKey.create(Registries.ITEM, id), TagKey.create(Registries.BLOCK, id));
    }

    public Ingredient ingredient() {
        return Ingredient.of(itemTag);
    }
}
