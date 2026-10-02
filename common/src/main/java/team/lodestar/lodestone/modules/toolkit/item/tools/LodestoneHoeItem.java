package team.lodestar.lodestone.modules.toolkit.item.tools;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import team.lodestar.lodestone.modules.toolkit.item.*;

public class LodestoneHoeItem extends HoeItem {

    public LodestoneHoeItem(Tier tier, float attackDamage, float attackSpeed, LodestoneItemProperties properties) {
        super(tier, properties.mergeAttributes(
                ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, tier.getAttackDamageBonus() + attackDamage - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed - 4f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .build()));
    }
}

