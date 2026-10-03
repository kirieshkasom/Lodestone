package team.lodestar.lodestone.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.fabric.FabricCategorizedCreativeTab;

import java.util.Locale;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CategorizedCreativeScreenMixin {
    @Shadow private static CreativeModeTab selectedTab;
    @Shadow private EditBox searchBox;
    @Shadow private float scrollOffs;

    @Redirect(method = {"selectTab", "charTyped", "keyPressed", "keyReleased", "renderBg", "getTooltipFromContainerItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;"), require = 0)
    private CreativeModeTab.Type lodestone$searchableTab(CreativeModeTab tab) {
        return tab instanceof FabricCategorizedCreativeTab categorized && categorized.settings().hasSearchBar() ? CreativeModeTab.Type.SEARCH : tab.getType();
    }

    @Inject(method = "selectTab", at = @At("TAIL"))
    private void lodestone$searchWidth(CreativeModeTab tab, CallbackInfo callback) {
        int width = tab instanceof FabricCategorizedCreativeTab categorized ? categorized.settings().searchBarWidth() : 89;
        searchBox.setX(searchBox.getX() + searchBox.getWidth() - width);
        searchBox.setWidth(width);
    }

    @Inject(method = "refreshSearchResults", at = @At("HEAD"), cancellable = true)
    private void lodestone$searchOwnContents(CallbackInfo callback) {
        if (!(selectedTab instanceof FabricCategorizedCreativeTab categorized) || !categorized.settings().hasSearchBar()) {
            return;
        }
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;
        String query = searchBox.getValue().toLowerCase(Locale.ROOT);
        screen.getMenu().items.clear();
        for (ItemStack stack : selectedTab.getSearchTabDisplayItems()) {
            boolean matches;
            if (query.startsWith("#")) {
                String tagQuery = query.substring(1);
                matches = stack.getTags().anyMatch(tag -> tag.location().toString().contains(tagQuery));
            } else {
                matches = query.isEmpty() || BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().contains(query)
                        || stack.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL).stream().anyMatch(line -> line.getString().toLowerCase(Locale.ROOT).contains(query));
            }
            if (matches) {
                screen.getMenu().items.add(stack);
            }
        }
        scrollOffs = 0;
        screen.getMenu().scrollTo(0);
        callback.cancel();
    }

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 4210752))
    private int lodestone$labelColor(int original) {
        return selectedTab instanceof FabricCategorizedCreativeTab categorized ? categorized.settings().labelColor() : original;
    }

    @WrapOperation(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void lodestone$scroller(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (selectedTab instanceof FabricCategorizedCreativeTab categorized && categorized.settings().scrollerSprite() != null) {
            sprite = categorized.settings().scrollerSprite();
        }
        original.call(graphics, sprite, x, y, width, height);
    }

    @WrapOperation(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void lodestone$tabImage(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original, @Local(argsOnly = true) CreativeModeTab tab) {
        if (tab instanceof FabricCategorizedCreativeTab categorized && categorized.settings().tabsImage() != null) {
            int u = tab.column() * 28;
            int v = (tab.row() == CreativeModeTab.Row.TOP ? 0 : 64) + (tab == selectedTab ? 32 : 0);
            graphics.blit(categorized.settings().tabsImage(), x, y, u, v, width, height);
        } else {
            original.call(graphics, sprite, x, y, width, height);
        }
    }
}
