package team.lodestar.lodestone.modules.toolkit.creative_tab.entries;

import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotLocation;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotStorage;

public abstract class CreativeTabCategoryEntry {

    protected CreativeTabCategoryEntry() {

    }

    public abstract SlotStorage bake(CategorizedCreativeTab tab, SlotLocation location);

}
