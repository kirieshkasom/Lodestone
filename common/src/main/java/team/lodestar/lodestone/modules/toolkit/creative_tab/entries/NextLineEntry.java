package team.lodestar.lodestone.modules.toolkit.creative_tab.entries;

import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotLocation;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotStorage;

public class NextLineEntry extends CreativeTabCategoryEntry {

    public static final NextLineEntry INSTANCE = new NextLineEntry();

    protected NextLineEntry() {
        super();
    }

    @Override
    public SlotStorage bake(CategorizedCreativeTab tab, SlotLocation location) {
        location.nextLine(false);
        return null;
    }
}
