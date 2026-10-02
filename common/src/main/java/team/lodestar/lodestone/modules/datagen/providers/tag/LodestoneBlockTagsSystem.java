package team.lodestar.lodestone.modules.datagen.providers.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BlockTags.*;

@SuppressWarnings("unused")
public abstract class LodestoneBlockTagsSystem extends BlockTagsProvider {

    public LodestoneBlockTagsSystem(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    public void addTagsFromBlockProperties(Set<DeferredHolder<Block, ? extends Block>> blocks) {
        var blockList = sorted(blocks);
        for (Block block : blockList) {
            LodestoneBlockProperties properties = (LodestoneBlockProperties) block.properties();
            LodestoneDatagenBlockData data = properties.getDatagenData();
            for (TagKey<Block> tag : data.getTags()) {
                tag(tag).add(block);
            }
            addCommonTags(block);
        }
    }

    public void addCommonTags(Block block) {
        addNameTag(PLANKS, block, "planks");
        addNameTag(LOGS, block, RotatedPillarBlock.class, "log");
        addNameTag(Tags.Blocks.STRIPPED_LOGS, block, RotatedPillarBlock.class, "log");
        addNameTag(Tags.Blocks.STRIPPED_WOODS, block, RotatedPillarBlock.class, "stripped", "wood");

        addClassTag(BUTTONS, WOODEN_BUTTONS, block, ButtonBlock.class);
        addClassTag(PRESSURE_PLATES, WOODEN_PRESSURE_PLATES, block, PressurePlateBlock.class);
        addClassTag(DOORS, WOODEN_DOORS, block, DoorBlock.class);
        addClassTag(STAIRS, WOODEN_STAIRS, block, StairBlock.class);
        addClassTag(SLABS, WOODEN_SLABS, block, SlabBlock.class);
        addClassTag(TRAPDOORS, WOODEN_TRAPDOORS, block, TrapDoorBlock.class);
        addClassTag(FENCES, WOODEN_FENCES, block, FenceBlock.class);
        addClassTag(CEILING_HANGING_SIGNS, block, CeilingHangingSignBlock.class);
        addClassTag(WALL_HANGING_SIGNS, block, WallHangingSignBlock.class);

        addClassTag(SAPLINGS, block, SaplingBlock.class);
        addClassTag(WALLS, block, WallBlock.class);
        addClassTag(LEAVES, block, LeavesBlock.class);
        addClassTag(STANDING_SIGNS, block, StandingSignBlock.class);
        addClassTag(WALL_SIGNS, block, WallSignBlock.class);
        addClassTag(CROPS, block, CropBlock.class);
        addClassTag(FENCE_GATES, block, FenceGateBlock.class);
        addClassTag(CAULDRONS, block, AbstractCauldronBlock.class);

        addConditionTag(REPLACEABLE, block, block.defaultBlockState().canBeReplaced());

    }

    public void addClassTag(TagKey<Block> tagKey, TagKey<Block> woodenKey, Block block, Class<? extends Block> clazz) {
        boolean condition = clazz.isInstance(block);
        addConditionTag(tagKey, block, condition);
        if (condition) {
            addWoodenTag(woodenKey, block);
        }
    }

    public void addClassTag(TagKey<Block> tagKey, Block block, Class<? extends Block> clazz) {
        addConditionTag(tagKey, block, clazz.isInstance(block));
    }

    public void addWoodenTag(TagKey<Block> tagKey, Block block) {
        addNameTag(tagKey, block, "wood", "planks");
    }

    public void addNameTag(TagKey<Block> tagKey, Block block, String... checks) {
        addNameTag(tagKey, block, Block.class, checks);
    }

    @SuppressWarnings({"deprecation", "DataFlowIssue"})
    public void addNameTag(TagKey<Block> tagKey, Block block, Class<? extends Block> clazz, String... checks) {
        if (clazz.isInstance(block)) {
            var name = block.builtInRegistryHolder().getKey().location().getPath();
            boolean matches = checks.length == 0 || Arrays.stream(checks).anyMatch(name::contains);
            addConditionTag(tagKey, block, matches);
        }
    }

    public void addConditionTag(TagKey<Block> tagKey, Block block, boolean condition) {
        if (condition) {
            tag(tagKey).add(block);
        }
    }

    public static List<? extends Block> sorted(Set<DeferredHolder<Block, ? extends Block>> blocks) {
        return blocks.stream().map(DeferredHolder::get).sorted(Comparator.comparingInt(BuiltInRegistries.BLOCK::getId)).toList();
    }
}
