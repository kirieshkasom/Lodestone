package team.lodestar.lodestone.modules.datagen.smith.itemmodel.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.ItemModelSmith;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.ItemModelSmithResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DatagenItemQuery {

    protected final List<Item> items = new ArrayList<>();

    public static DatagenItemQuery fromList(ItemLike... items) {
        return new DatagenItemQuery(Arrays.stream(items).map(ItemLike::asItem));
    }

    public DatagenItemQuery(Stream<Item> items) {
        items.forEach(this.items::add);
    }

    public List<Item> getItems() {
        return items;
    }
}
