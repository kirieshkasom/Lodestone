package team.lodestar.lodestone.modules.toolkit.blockentity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTicker.Type;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class LodestoneBlockEntityTypeBuilder<T extends LodestoneBlockEntity> {
    private final LodestoneBlockEntitySupplier<? extends T> factory;
    private final Set<Supplier<? extends Block>> validBlocks;
    private Type type = Type.NONE;

    private LodestoneBlockEntityTypeBuilder(LodestoneBlockEntitySupplier<? extends T> factory, Set<Supplier<? extends Block>> validBlocks) {
        this.factory = factory;
        this.validBlocks = validBlocks;
    }

    @SafeVarargs
    public static <T extends LodestoneBlockEntity> LodestoneBlockEntityTypeBuilder<T> create(LodestoneBlockEntitySupplier<? extends T> factory, Supplier<? extends Block>... validBlocks) {
        return new LodestoneBlockEntityTypeBuilder<>(factory, ImmutableSet.copyOf(validBlocks));
    }

    public static <T extends LodestoneBlockEntity> LodestoneBlockEntityTypeBuilder<T> create(LodestoneBlockEntitySupplier<? extends T> factory, Block... validBlocks) {
        Set<Supplier<? extends Block>> suppliers = new HashSet<>();
        for (Block validBlock : validBlocks) {
            suppliers.add(() -> validBlock);
        }
        return new LodestoneBlockEntityTypeBuilder<>(factory, ImmutableSet.copyOf(suppliers));
    }

    public LodestoneBlockEntityTypeBuilder<T> setTickerType(Type type) {
        this.type = type;
        return this;
    }

    public LodestoneBlockEntityType<T> build() {
        Set<Block> blocks = validBlocks.stream().map(Supplier::get).collect(Collectors.toSet());
        return new LodestoneBlockEntityType<>(factory, blocks, type);
    }

    public interface LodestoneBlockEntitySupplier<T extends LodestoneBlockEntity> extends BlockEntityType.BlockEntitySupplier<T> {
    }
}