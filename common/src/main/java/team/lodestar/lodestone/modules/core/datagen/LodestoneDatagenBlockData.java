package team.lodestar.lodestone.modules.core.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import team.lodestar.lodestone.modules.core.util.BlockItemTagKey;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties.BlockRenderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@DatagenOnly
public class LodestoneDatagenBlockData {

    private static HashMap<LodestoneBlockProperties, LodestoneDatagenBlockData> DATAGEN_DATA_CACHE;

    private final List<TagKey<Block>> tags = new ArrayList<>();

    public ResourceLocation renderType;
    public boolean noLootDatagen = false;

    public static LodestoneDatagenBlockData copyDatagenDataFrom(LodestoneBlockProperties from, LodestoneBlockProperties to) {
        var copy = getDatagenData(from).copy();
        return DATAGEN_DATA_CACHE.put(to, copy);
    }

    public static LodestoneDatagenBlockData getDatagenData(LodestoneBlockProperties properties) {
        if (!DatagenModLoader.isRunningDataGen()) {
            throw new UnsupportedOperationException("Cannot access datagen data outside of datagen");
        }
        if (DATAGEN_DATA_CACHE == null) {
            DATAGEN_DATA_CACHE = new HashMap<>();
        }
        return DATAGEN_DATA_CACHE.computeIfAbsent(properties, p -> new LodestoneDatagenBlockData());
    }

    public LodestoneDatagenBlockData addTag(TagKey<Block> blockTagKey) {
        tags.add(blockTagKey);
        return this;
    }

    @SafeVarargs
    public final LodestoneDatagenBlockData addTags(TagKey<Block>... blockTagKeys) {
        tags.addAll(Arrays.asList(blockTagKeys));
        return this;
    }

    public LodestoneDatagenBlockData addTag(BlockItemTagKey key) {
        addTag(key.blockTag());
        return this;
    }

    public final LodestoneDatagenBlockData addTags(BlockItemTagKey... blockTagKeys) {
        for (BlockItemTagKey key : blockTagKeys) {
            addTag(key.blockTag());
        }
        return this;
    }

    public LodestoneDatagenBlockData copy() {
        LodestoneDatagenBlockData copy = new LodestoneDatagenBlockData();
        copy.tags.addAll(tags);
        copy.renderType = renderType;
        copy.noLootDatagen = noLootDatagen;
        return copy;
    }

    public List<TagKey<Block>> getTags() {
        return tags;
    }

    public LodestoneDatagenBlockData needsPickaxe() {
        return addTag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public LodestoneDatagenBlockData needsAxe() {
        return addTag(BlockTags.MINEABLE_WITH_AXE);
    }

    public LodestoneDatagenBlockData needsShovel() {
        return addTag(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public LodestoneDatagenBlockData needsHoe() {
        return addTag(BlockTags.MINEABLE_WITH_HOE);
    }

    public LodestoneDatagenBlockData needsStone() {
        return addTag(BlockTags.NEEDS_STONE_TOOL);
    }

    public LodestoneDatagenBlockData needsIron() {
        return addTag(BlockTags.NEEDS_IRON_TOOL);
    }

    public LodestoneDatagenBlockData needsDiamond() {
        return addTag(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    public LodestoneDatagenBlockData setRenderType(BlockRenderType renderType) {
        this.renderType = renderType.getLocation();
        return this;
    }

    public LodestoneDatagenBlockData noLootDatagen() {
        noLootDatagen = true;
        return this;
    }
}