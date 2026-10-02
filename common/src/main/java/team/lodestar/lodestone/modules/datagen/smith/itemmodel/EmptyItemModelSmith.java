package team.lodestar.lodestone.modules.datagen.smith.itemmodel;

import net.minecraft.world.item.Item;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelSystem;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.data.DatagenItemQuery;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.data.ItemModelSystemData;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EmptyItemModelSmith extends ItemModelSmith{
    public EmptyItemModelSmith() {
        super(null);
    }

    @Override
    public List<ItemModelSmithResult> act(ItemModelSystemData data, DatagenItemQuery queried) {
        return Collections.emptyList();
    }
}
