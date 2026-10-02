package team.lodestar.lodestone.modules.datagen.smith.blockstate;

import net.minecraft.world.level.block.Block;
import team.lodestar.lodestone.modules.datagen.providers.block.LodestoneBlockStateSystem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record BlockStateSystemData<T extends LodestoneBlockStateSystem>(T provider, Consumer<Supplier<? extends Block>> consumer) {
}
