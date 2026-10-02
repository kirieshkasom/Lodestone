package team.lodestar.lodestone.modules.core.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record BlockItemTagKey(TagKey<Item> itemTag, TagKey<Block> blockTag) {

    public BlockItemTagKey(ResourceLocation id) {
        this(ItemTags.create(id), BlockTags.create(id));
    }

    public Ingredient ingredient() {
        return Ingredient.of(itemTag);
    }
}