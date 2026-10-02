package team.lodestar.lodestone.internal.registration;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTicker;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;
import team.lodestar.lodestone.modules.toolkit.multiblock.ILodestoneMultiblockComponent;
import team.lodestar.lodestone.modules.toolkit.multiblock.MultiBlockComponentEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class LodestoneBlockEntityTypes {
    private static final Set<Block> MULTIBLOCK_BLOCKS = new HashSet<>();
    public static final RegistryEntry<LodestoneBlockEntityType<MultiBlockComponentEntity>> MULTIBLOCK_COMPONENT = new RegistryEntry<>(LodestoneCommon.lodestonePath("multiblock_component"), LodestoneBlockEntityTypes::createMultiblockType);

    private LodestoneBlockEntityTypes() {
    }

    private static LodestoneBlockEntityType<MultiBlockComponentEntity> createMultiblockType() {
        MULTIBLOCK_BLOCKS.addAll(Arrays.asList(getBlocks(ILodestoneMultiblockComponent.class)));
        return new LodestoneBlockEntityType<>(MultiBlockComponentEntity::new, MULTIBLOCK_BLOCKS, LodestoneBlockEntityTicker.Type.SERVER);
    }

    public static void register(RegistryRegistrar<BlockEntityType<?>> registrar) {
        registrar.register(MULTIBLOCK_COMPONENT);
    }

    public static void blockRegistered(Block block) {
        if (block instanceof ILodestoneMultiblockComponent) {
            MULTIBLOCK_BLOCKS.add(block);
            if (block instanceof LodestoneEntityBlock<?> entityBlock) {
                entityBlock.setBlockEntity(MULTIBLOCK_COMPONENT.get());
            }
        }
    }

    public static Block[] getBlocks(Class<?>... blockClasses) {
        DefaultedRegistry<Block> blocks = BuiltInRegistries.BLOCK;
        ArrayList<Block> matchingBlocks = new ArrayList<>();
        for (Block block : blocks) {
            if (Arrays.stream(blockClasses).anyMatch(b -> b.isInstance(block))) {
                matchingBlocks.add(block);
            }
        }
        return matchingBlocks.toArray(new Block[0]);
    }

    public static Block[] getBlocksExact(Class<?> clazz) {
        DefaultedRegistry<Block> blocks = BuiltInRegistries.BLOCK;
        ArrayList<Block> matchingBlocks = new ArrayList<>();
        for (Block block : blocks) {
            if (clazz.equals(block.getClass())) {
                matchingBlocks.add(block);
            }
        }
        return matchingBlocks.toArray(new Block[0]);
    }
}
