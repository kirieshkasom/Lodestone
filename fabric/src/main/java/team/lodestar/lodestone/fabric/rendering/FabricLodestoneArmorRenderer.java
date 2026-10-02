package team.lodestar.lodestone.fabric.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import team.lodestar.lodestone.modules.rendering.model.entity.armor.LodestoneArmorClientItemExtensions;

public final class FabricLodestoneArmorRenderer {
    private FabricLodestoneArmorRenderer() {
    }

    /** Registers a model-backed renderer for the supplied armor items. */
    public static void register(LodestoneArmorClientItemExtensions extensions, Item... items) {
        ArmorRenderer.register((poseStack, bufferSource, itemStack, entity, slot, packedLight, contextModel) ->
                render(extensions, poseStack, bufferSource, itemStack, entity, slot, packedLight, contextModel), items);
    }

    private static void render(LodestoneArmorClientItemExtensions extensions, PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemStack, LivingEntity entity, EquipmentSlot slot, int packedLight, HumanoidModel<?> contextModel) {
        if (!(itemStack.getItem() instanceof ArmorItem armorItem) || armorItem.getEquipmentSlot() != slot) {
            return;
        }

        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        Model model = extensions.getGenericArmorModel(entity, itemStack, slot, contextModel, Minecraft.getInstance().getEntityModels(), partialTicks);
        Holder<ArmorMaterial> material = armorItem.getMaterial();
        boolean innerLayer = slot == EquipmentSlot.LEGS;
        int dyeColor = itemStack.is(ItemTags.DYEABLE)
                ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, DyedItemColor.LEATHER_COLOR))
                : -1;

        for (ArmorMaterial.Layer layer : material.value().layers()) {
            int color = layer.dyeable() ? dyeColor : -1;
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(layer.texture(innerLayer)));
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        }

        ArmorTrim trim = itemStack.get(DataComponents.TRIM);
        if (trim != null) {
            ResourceLocation trimTexture = innerLayer ? trim.innerTexture(armorItem.getMaterial()) : trim.outerTexture(armorItem.getMaterial());
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(trimTexture);
            VertexConsumer trimConsumer = sprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
            model.renderToBuffer(poseStack, trimConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (itemStack.hasFoil()) {
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
