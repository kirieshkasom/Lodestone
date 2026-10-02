package team.lodestar.lodestone.modules.core.attribute;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;

public interface LodestoneAttributeFormatting {
    ResourceLocation getBaseId();

    MutableComponent toValueComponent(AttributeModifier.Operation operation, double value, TooltipFlag flag);
}
