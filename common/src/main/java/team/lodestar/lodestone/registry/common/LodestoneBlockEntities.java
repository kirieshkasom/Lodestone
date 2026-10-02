package team.lodestar.lodestone.registry.common;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTypeBuilder;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTicker;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;
import team.lodestar.lodestone.modules.toolkit.multiblock.ILodestoneMultiblockComponent;
import team.lodestar.lodestone.modules.toolkit.multiblock.MultiBlockComponentEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import static team.lodestar.lodestone.LodestoneLib.LODESTONE;


public class LodestoneBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LODESTONE);

    public static final Supplier<LodestoneBlockEntityType<MultiBlockComponentEntity>> MULTIBLOCK_COMPONENT = BLOCK_ENTITY_TYPES.register("multiblock_component", () -> LodestoneBlockEntityTypeBuilder.create(MultiBlockComponentEntity::new, getBlocks(ILodestoneMultiblockComponent.class)).setTickerType(LodestoneBlockEntityTicker.Type.SERVER).build());

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