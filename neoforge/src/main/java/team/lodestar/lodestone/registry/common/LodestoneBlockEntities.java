package team.lodestar.lodestone.registry.common;

import team.lodestar.lodestone.internal.registration.LodestoneBlockEntityTypes;
import team.lodestar.lodestone.neoforge.NeoForgeRegistryRegistrar;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;
import team.lodestar.lodestone.modules.toolkit.multiblock.ILodestoneMultiblockComponent;
import team.lodestar.lodestone.modules.toolkit.multiblock.MultiBlockComponentEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import static team.lodestar.lodestone.internal.LodestoneCommon.LODESTONE;


public class LodestoneBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LODESTONE);

    public static final Supplier<LodestoneBlockEntityType<MultiBlockComponentEntity>> MULTIBLOCK_COMPONENT = new NeoForgeRegistryRegistrar<>(BLOCK_ENTITY_TYPES).register(LodestoneBlockEntityTypes.MULTIBLOCK_COMPONENT);

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