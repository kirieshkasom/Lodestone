package team.lodestar.lodestone.modules.toolkit.block;


import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.*;

public record BlockBlockItemHolder<T extends Block, K extends BlockItem>(DeferredBlock<T> block, DeferredItem<K> item) implements Supplier<T>, ItemLike {

    @Override
    public T get() {
        return block.get();
    }

    @Override
    public @NotNull Item asItem() {
        return getItem();
    }

    public BlockState getDefaultState() {
        return get().defaultBlockState();
    }

    public ItemStack getDefaultInstance() {
        return getItem().getDefaultInstance();
    }

    public K getItem() {
        return item.get();
    }

    public DeferredHolder<Block, T> getBlockHolder() {
        return block;
    }

    public DeferredHolder<Item, K> getItemHolder() {
        return item;
    }

    public boolean is(ItemStack stack) {
        return is(stack.getItem());
    }

    public boolean is(ItemLike item) {
        return getItem().equals(item.asItem());
    }

    public boolean is(BlockState state) {
        return is(state.getBlock());
    }

    public boolean is(Block block) {
        return get().equals(block);
    }
}