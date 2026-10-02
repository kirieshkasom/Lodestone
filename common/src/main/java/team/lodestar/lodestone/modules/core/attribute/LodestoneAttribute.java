package team.lodestar.lodestone.modules.core.attribute;

import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

public class LodestoneAttribute extends Attribute implements LodestoneAttributeFormatting {

    private final ResourceLocation baseId;

    private final boolean forcePercentage;

    public static LodestoneAttributeBuilder create(ResourceLocation id, double defaultValue) {
        return new LodestoneAttributeBuilder(id, defaultValue);
    }
    protected LodestoneAttribute(ResourceLocation id, ResourceLocation baseId, double defaultValue, boolean forcePercentage) {
        super("attribute.name." + id.getNamespace() + "." + id.getPath(), defaultValue);
        this.baseId = baseId;
        this.forcePercentage = forcePercentage;
    }

    @Nullable
    public ResourceLocation getBaseId() {
        return baseId;
    }

    public @NotNull MutableComponent toValueComponent(@Nullable AttributeModifier.Operation op, double value, TooltipFlag flag) {
        return AttributeValueFormatter.format(op, value, forcePercentage);
    }

}
