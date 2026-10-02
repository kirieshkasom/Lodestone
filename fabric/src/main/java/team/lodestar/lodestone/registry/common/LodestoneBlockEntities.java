package team.lodestar.lodestone.registry.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;
import team.lodestar.lodestone.modules.toolkit.multiblock.MultiBlockComponentEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.fabric.FabricRegistryRegistrar;
import team.lodestar.lodestone.internal.registration.RegistryRegistrar;
import team.lodestar.lodestone.internal.registration.LodestoneBlockEntityTypes;
import java.util.function.Supplier;

public class LodestoneBlockEntities {
    public static final RegistryRegistrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new FabricRegistryRegistrar<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final Supplier<LodestoneBlockEntityType<MultiBlockComponentEntity>> MULTIBLOCK_COMPONENT = LodestoneBlockEntityTypes.MULTIBLOCK_COMPONENT;

    public static Block[] getBlocks(Class<?>... blockClasses) {
        return LodestoneBlockEntityTypes.getBlocks(blockClasses);
    }

    public static Block[] getBlocksExact(Class<?> clazz) {
        return LodestoneBlockEntityTypes.getBlocksExact(clazz);
    }
}
