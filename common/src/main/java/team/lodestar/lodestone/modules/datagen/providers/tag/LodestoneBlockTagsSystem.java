package team.lodestar.lodestone.modules.datagen.providers.tag;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class LodestoneBlockTagsSystem extends LodestoneTagProvider<Block> {
    private static final TagKey<Block> STRIPPED_LOGS = commonTag("stripped_logs");
    private static final TagKey<Block> STRIPPED_WOODS = commonTag("stripped_woods");

    protected LodestoneBlockTagsSystem(PackOutput output, String modId) {
        super(output, "block", modId + " Block Tags", block -> BuiltInRegistries.BLOCK.getKey(block));
    }

    public void addTagsFromBlockProperties(Collection<? extends Supplier<? extends Block>> blocks) {
        List<? extends Block> sortedBlocks = sorted(blocks);
        for (Block block : sortedBlocks) {
            LodestoneBlockProperties properties = (LodestoneBlockProperties) block.properties();
            LodestoneDatagenBlockData data = properties.getDatagenData();
            for (TagKey<Block> blockTag : data.getTags()) {
                tag(blockTag).add(block);
            }
            addCommonTags(block);
        }
    }

    public void addCommonTags(Block block) {
        addNameTag(BlockTags.PLANKS, block, "planks");
        addNameTag(BlockTags.LOGS, block, RotatedPillarBlock.class, "log");
        addNameTag(STRIPPED_LOGS, block, RotatedPillarBlock.class, "log");
        addNameTag(STRIPPED_WOODS, block, RotatedPillarBlock.class, "stripped", "wood");

        addClassTag(BlockTags.BUTTONS, BlockTags.WOODEN_BUTTONS, block, ButtonBlock.class);
        addClassTag(BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_PRESSURE_PLATES, block, PressurePlateBlock.class);
        addClassTag(BlockTags.DOORS, BlockTags.WOODEN_DOORS, block, DoorBlock.class);
        addClassTag(BlockTags.STAIRS, BlockTags.WOODEN_STAIRS, block, StairBlock.class);
        addClassTag(BlockTags.SLABS, BlockTags.WOODEN_SLABS, block, SlabBlock.class);
        addClassTag(BlockTags.TRAPDOORS, BlockTags.WOODEN_TRAPDOORS, block, TrapDoorBlock.class);
        addClassTag(BlockTags.FENCES, BlockTags.WOODEN_FENCES, block, FenceBlock.class);
        addClassTag(BlockTags.CEILING_HANGING_SIGNS, block, CeilingHangingSignBlock.class);
        addClassTag(BlockTags.WALL_HANGING_SIGNS, block, WallHangingSignBlock.class);

        addClassTag(BlockTags.SAPLINGS, block, SaplingBlock.class);
        addClassTag(BlockTags.WALLS, block, WallBlock.class);
        addClassTag(BlockTags.LEAVES, block, LeavesBlock.class);
        addClassTag(BlockTags.STANDING_SIGNS, block, StandingSignBlock.class);
        addClassTag(BlockTags.WALL_SIGNS, block, WallSignBlock.class);
        addClassTag(BlockTags.CROPS, block, CropBlock.class);
        addClassTag(BlockTags.FENCE_GATES, block, FenceGateBlock.class);
        addClassTag(BlockTags.CAULDRONS, block, AbstractCauldronBlock.class);

        addConditionTag(BlockTags.REPLACEABLE, block, block.defaultBlockState().canBeReplaced());
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

    @SuppressWarnings("deprecation")
    public void addNameTag(TagKey<Block> tagKey, Block block, Class<? extends Block> clazz, String... checks) {
        if (clazz.isInstance(block)) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            String name = blockId.getPath();
            boolean matches = checks.length == 0 || Arrays.stream(checks).anyMatch(name::contains);
            addConditionTag(tagKey, block, matches);
        }
    }

    public void addConditionTag(TagKey<Block> tagKey, Block block, boolean condition) {
        if (condition) {
            tag(tagKey).add(block);
        }
    }

    public static List<? extends Block> sorted(Collection<? extends Supplier<? extends Block>> blocks) {
        return blocks.stream().map(Supplier::get).sorted(Comparator.comparingInt(BuiltInRegistries.BLOCK::getId)).toList();
    }

    private static TagKey<Block> commonTag(String id) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", id));
    }
}
