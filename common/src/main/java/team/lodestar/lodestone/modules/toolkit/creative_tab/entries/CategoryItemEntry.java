package team.lodestar.lodestone.modules.toolkit.creative_tab.entries;

import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotLocation;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotStorage;

import java.util.function.Supplier;

public class CategoryItemEntry extends CreativeTabCategoryEntry {

    protected final Supplier<ItemStack> stackSupplier;

    public CategoryItemEntry(Supplier<ItemStack> stackSupplier) {
        super();
        this.stackSupplier = stackSupplier;
    }

    @Override
    public SlotStorage bake(CategorizedCreativeTab tab, SlotLocation location) {
        return new SlotStorage(location, stackSupplier.get());
    }
}