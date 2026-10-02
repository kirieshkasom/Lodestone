package team.lodestar.lodestone.fabric.mixin.gameplay;

import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.toolkit.enchanting.LodestoneSlotBasedEnchantmentAttributeEffect;
import team.lodestar.lodestone.modules.core.attribute.LodestoneAttributeFormatting;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackEnchantmentAttributeMixin {
    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void addSlotBasedEnchantmentModifiers(EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> consumer, CallbackInfo callback) {
        ItemStack stack = (ItemStack) (Object) this;
        LodestoneSlotBasedEnchantmentAttributeEffect.modifyAttributes(stack, appliedModifier -> {
            if (appliedModifier.slot().equals(slotGroup)) {
                consumer.accept(appliedModifier.attribute(), appliedModifier.modifier());
            }
        });
    }

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void addSlotBasedEnchantmentModifiers(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> consumer, CallbackInfo callback) {
        ItemStack stack = (ItemStack) (Object) this;
        LodestoneSlotBasedEnchantmentAttributeEffect.modifyAttributes(stack, appliedModifier -> {
            if (appliedModifier.slot().test(slot)) {
                consumer.accept(appliedModifier.attribute(), appliedModifier.modifier());
            }
        });
    }

    @Inject(method = "addModifierTooltip", at = @At("HEAD"), cancellable = true)
    private void formatLodestoneAttributeModifier(Consumer<Component> tooltipAdder, Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo callback) {
        if (!(attribute.value() instanceof LodestoneAttributeFormatting formatting)) {
            return;
        }
        boolean baseModifier = formatting.getBaseId() != null && modifier.id().equals(formatting.getBaseId());
        double amount = modifier.amount();
        if (baseModifier && player != null) {
            amount += player.getAttributeBaseValue(attribute);
        }
        Component value = formatting.toValueComponent(baseModifier ? null : modifier.operation(), amount, TooltipFlag.NORMAL);
        if (baseModifier) {
            tooltipAdder.accept(CommonComponents.space()
                    .append(Component.translatable("attribute.modifier.equals.0", value, Component.translatable(attribute.value().getDescriptionId())))
                    .withStyle(ChatFormatting.DARK_GREEN));
            callback.cancel();
            return;
        }
        if (amount > 0.0) {
            tooltipAdder.accept(Component.translatable("attribute.modifier.plus.0", value, Component.translatable(attribute.value().getDescriptionId()))
                    .withStyle(attribute.value().getStyle(true)));
        } else if (amount < 0.0) {
            tooltipAdder.accept(Component.translatable("attribute.modifier.take.0", formatting.toValueComponent(modifier.operation(), -amount, TooltipFlag.NORMAL), Component.translatable(attribute.value().getDescriptionId()))
                    .withStyle(attribute.value().getStyle(false)));
        }
        callback.cancel();
    }
}
