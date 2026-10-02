package team.lodestar.lodestone.modules.datagen.smith.itemmodel.data;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelSystem;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.EmptyItemModelSmith;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.ItemModelSmith;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.ItemModelSmithResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemModelSystemData {
    private final LodestoneItemModelSystem provider;
    private final Set<Item> items;

    public ItemModelSystemData(LodestoneItemModelSystem provider, DeferredRegister.Items items) {
        this(provider, items.getEntries());
    }

    public ItemModelSystemData(LodestoneItemModelSystem provider, Collection<? extends Supplier<? extends Item>> items) {
        this.provider = provider;
        this.items = items.stream().map(Supplier::get).filter(i -> !(i instanceof BlockItem)).collect(Collectors.toSet());
    }

    public LodestoneItemModelSystem provider() {
        return provider;
    }

    public Set<Item> items() {
        return items;
    }

    public DatagenItemQuery ofClass(Class<? extends Item> clazz) {
        return new DatagenItemQuery(items.stream().filter(clazz::isInstance));
    }

    public DatagenItemQuery allRemaining() {
        return new DatagenItemQuery(items.stream());
    }

    public Optional<ItemModelSmithResult> approveAct(ItemModelSmith smith, Item item) {
        if (smith instanceof EmptyItemModelSmith) {
            return Optional.empty();
        }
        var result = smith.act(provider, item);
        items.remove(item);
        return Optional.of(result);
    }
}