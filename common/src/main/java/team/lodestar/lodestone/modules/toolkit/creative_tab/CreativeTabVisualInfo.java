package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotStorage;

import java.util.Optional;

public abstract class CreativeTabVisualInfo {

    public abstract Optional<ResourceLocation> getHeaderTexture(CreativeTabHeader header, int row, int column);

    public abstract Optional<ResourceLocation> getEmptySlotTexture(SlotStorage slot);

    public abstract void drawHeaderSlot(GuiGraphics guiGraphics, int x, int y, TextureAtlasSprite sprite);

    public abstract void drawEmptySlot(GuiGraphics guiGraphics, int x, int y, TextureAtlasSprite sprite);

    public void drawHeaderText(GuiGraphics guiGraphics, Font font, Component title, int x, int y) {
        guiGraphics.drawString(font, title, x, y, 4210752, false);
    }
}
