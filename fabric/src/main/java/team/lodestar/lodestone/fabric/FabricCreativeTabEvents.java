package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.modules.toolkit.item.LodestoneItemProperties;

import java.util.Collection;

public final class FabricCreativeTabEvents {
    private FabricCreativeTabEvents() {
    }

    public static void register() {
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(FabricCreativeTabEvents::modifyEntries);
    }

    private static void modifyEntries(CreativeModeTab tab, FabricItemGroupEntries entries) {
        CategorizedCreativeTab.buildCreativeTabs(tab, entries::accept);
        ResourceKey<CreativeModeTab> tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).orElse(null);
        if (tabKey == null) {
            return;
        }
        Collection<ItemStack> parentEntries = entries.getDisplayStacks();
        LodestoneItemProperties.buildCreativeTabs(tab, tabKey, parentEntries, entries::accept);
    }
}
