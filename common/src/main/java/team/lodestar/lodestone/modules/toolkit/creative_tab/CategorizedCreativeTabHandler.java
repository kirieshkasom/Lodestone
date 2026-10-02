package team.lodestar.lodestone.modules.toolkit.creative_tab;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.*;
import net.minecraft.util.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import team.lodestar.lodestone.modules.toolkit.creative_tab.entries.CreativeTabCategoryEntry;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotStorage;

import java.util.*;

public class CategorizedCreativeTabHandler {
    
    public static Optional<CategorizedCreativeTab> getOpenCategorizedTab() {
        var selectedTab = CreativeModeInventoryScreen.selectedTab;
        if (selectedTab instanceof CategorizedCreativeTab categorizedTab) {
            return Optional.of(categorizedTab);
        }
        return Optional.empty();
    }

    public static void modifyTab(CreativeModeInventoryScreen.ItemPickerMenu menu) {
        getOpenCategorizedTab().ifPresent(t -> fillMenu(menu, t));
    }

    public static boolean renderSlot(GuiGraphics guiGraphics, Slot slot) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof CreativeModeInventoryScreen screen) {
            if (!(slot instanceof CreativeModeInventoryScreen.CustomCreativeSlot)) {
                return false;
            }
            var optional = getOpenCategorizedTab();
            if (optional.isEmpty()) {
                return false;
            }
            var categorizedTab = optional.get();
            var visualInfo = categorizedTab.visualInfo;
            if (visualInfo == null) {
                return false;
            }
            var item = slot.getItem();
            if (item.isEmpty()) {
                var menu = screen.getMenu();
                int row = menu.getRowIndexForScroll(screen.scrollOffs) + slot.getSlotIndex() / 9;
                int column = slot.getContainerSlot() % 9;
                int itemIndex = row * 9 + column;
                var pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(0.0F, 0.0F, 100.0F);

                var header = categorizedTab.getHeader(row);
                if (header != null) {
                    var texture = visualInfo.getHeaderTexture(header, row, column);
                    if (texture.isPresent()) {
                        var sprite = minecraft.getGuiSprites().getSprite(texture.get());
                        visualInfo.drawHeaderSlot(guiGraphics, slot.x, slot.y, sprite);
                    }
                    if (column == 0) {
                        var font = minecraft.font;
                        var title = header.getHeaderText();
                        int x = slot.x + 80 - font.width(title) / 2;
                        int y = slot.y + 1;
                        pose.pushPose();
                        pose.translate(0.0F, 0.0F, 100.0F);
                        visualInfo.drawHeaderText(guiGraphics, font, title, x, y);
                        pose.popPose();
                    }
                }
                else {
                    var data = categorizedTab.getSlotStorage(itemIndex);
                    var texture = visualInfo.getEmptySlotTexture(data);
                    if (texture.isPresent()) {
                        var sprite = minecraft.getGuiSprites().getSprite(texture.get());
                        visualInfo.drawEmptySlot(guiGraphics, slot.x, slot.y, sprite);
                    }
                }
                pose.popPose();
                return true;
            }
        }
        return false;
    }

    public static boolean disableSlotHighlight(Slot slot) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof CreativeModeInventoryScreen) {
            if (!(slot instanceof CreativeModeInventoryScreen.CustomCreativeSlot)) {
                return false;
            }
            if (getOpenCategorizedTab().isEmpty()) {
                return false;
            }
            return slot.getItem().isEmpty();
        }
        return false;
    }

    public static void fillMenu(CreativeModeInventoryScreen.ItemPickerMenu menu, CategorizedCreativeTab tab) {
        var items = menu.items;
        tab.bakeCategoryData(tab);
        items.clear();

        var slots = tab.slots;
        int mostRecentIndex = 0;
        for (int index : slots.keySet()) {
            var slotStorage = slots.get(index);
            while (mostRecentIndex < slotStorage.getItemIndex()) {
                items.add(ItemStack.EMPTY);
                mostRecentIndex++;
            }
            items.add(slotStorage.getSelectedItem());
            mostRecentIndex++;
        }
    }
}