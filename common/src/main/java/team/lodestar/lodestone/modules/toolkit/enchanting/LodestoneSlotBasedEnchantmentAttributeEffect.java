package team.lodestar.lodestone.modules.toolkit.enchanting;

import team.lodestar.lodestone.internal.registration.LodestoneEnchantmentEffects;
import com.google.common.collect.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.phys.*;
import team.lodestar.lodestone.registry.common.tag.*;

import java.util.function.Consumer;

/**
 * @author SammySemicolon
 * This is a copy of {@link net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect} that adds the attributes to the item's slot group, as opposed to a generic one
 * The only difference is that the attribute gets added to the "When on X:" tooltip effectType as opposed to the "When Worn:" one
 */
public record LodestoneSlotBasedEnchantmentAttributeEffect(ResourceLocation id, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation)
    implements EnchantmentLocationBasedEffect {
    public static final MapCodec<LodestoneSlotBasedEnchantmentAttributeEffect> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(
                            ResourceLocation.CODEC.fieldOf("id").forGetter(LodestoneSlotBasedEnchantmentAttributeEffect::id),
                            Attribute.CODEC.fieldOf("attribute").forGetter(LodestoneSlotBasedEnchantmentAttributeEffect::attribute),
                            LevelBasedValue.CODEC.fieldOf("amount").forGetter(LodestoneSlotBasedEnchantmentAttributeEffect::amount),
                            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(LodestoneSlotBasedEnchantmentAttributeEffect::operation)
                    )
                    .apply(obj, LodestoneSlotBasedEnchantmentAttributeEffect::new)
    );

    public static void modifyAttributes(ItemStack stack, Consumer<AppliedModifier> consumer) {
        if (stack.is(LodestoneItemTags.ENCHANTMENT_HOLDER)) {
            return;
        }
        LodestoneEnchantmentDataHelper.runIterationOnItem(stack, null, (holder, level) -> {
            var enchantment = holder.value();
            enchantment.getEffects(LodestoneEnchantmentEffects.SLOT_BOUND_ATTRIBUTES.get()).forEach((effect) -> {
                Equipable equipable = Equipable.get(stack);
                EquipmentSlot equipmentSlot = equipable == null ? null : equipable.getEquipmentSlot();
                EquipmentSlotGroup group = null;
                if (equipmentSlot != null) {
                    EquipmentSlot finalEquipmentSlot = equipmentSlot;
                    if (enchantment.definition().slots().stream().anyMatch(g -> g.test(finalEquipmentSlot))) {
                        group = EquipmentSlotGroup.bySlot(equipmentSlot);
                    }
                }
                if (group == null) {
                    group = EquipmentSlotGroup.ANY;
                }
                AttributeModifier modifier = effect.getModifier(level, group);
                consumer.accept(new AppliedModifier(effect.attribute(), modifier, group));
            });
        });
    }

    public record AppliedModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
    }

    private ResourceLocation idForSlot(StringRepresentable slot) {
        return this.id.withSuffix("/" + slot.getSerializedName());
    }

    public AttributeModifier getModifier(int enchantmentLevel, StringRepresentable slot) {
        return new AttributeModifier(idForSlot(slot), amount().calculate(enchantmentLevel), operation());
    }

    @Override
    public void onChangedBlock(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 pos, boolean applyTransientEffects) {
        if (applyTransientEffects && entity instanceof LivingEntity livingentity) {
            livingentity.getAttributes().addTransientAttributeModifiers(makeAttributeMap(enchantmentLevel, item));
        }
    }

    @Override
    public void onDeactivated(EnchantedItemInUse item, Entity entity, Vec3 pos, int enchantmentLevel) {
        if (entity instanceof LivingEntity livingentity) {
            livingentity.getAttributes().removeAttributeModifiers(makeAttributeMap(enchantmentLevel, item));
        }
    }

    private HashMultimap<Holder<Attribute>, AttributeModifier> makeAttributeMap(int enchantmentLevel, EnchantedItemInUse item) {
        HashMultimap<Holder<Attribute>, AttributeModifier> hashmultimap = HashMultimap.create();
        hashmultimap.put(attribute, getModifier(enchantmentLevel, item.inSlot()));
        return hashmultimap;
    }

    @Override
    public MapCodec<LodestoneSlotBasedEnchantmentAttributeEffect> codec() {
        return CODEC;
    }

}
