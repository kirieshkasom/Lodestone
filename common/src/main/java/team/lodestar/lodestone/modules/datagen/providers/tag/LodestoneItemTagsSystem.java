package team.lodestar.lodestone.modules.datagen.providers.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;

import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class LodestoneItemTagsSystem extends LodestoneTagProvider<Item> {
    protected LodestoneItemTagsSystem(PackOutput output, String modId) {
        super(output, "item", modId + " Item Tags", item -> BuiltInRegistries.ITEM.getKey(item));
    }

    public TagAppender<Item> tag(TagKey<Item> key) {
        return super.tag(key);
    }

    public void addTagsFromBlockProperties(Collection<? extends Supplier<? extends Block>> blocks) {
        for (Block block : LodestoneBlockTagsSystem.sorted(blocks)) {
            Item item = block.asItem();
            if (item.equals(Items.AIR)) {
                continue;
            }
            LodestoneBlockProperties properties = (LodestoneBlockProperties) block.properties();
            LodestoneDatagenBlockData data = properties.getDatagenData();
            for (net.minecraft.tags.TagKey<Block> blockTag : data.getTags()) {
                TagKey<Item> itemTag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, blockTag.location());
                tag(itemTag).add(item);
            }
        }
    }
}
