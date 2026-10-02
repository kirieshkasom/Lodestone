package team.lodestar.lodestone.modules.core.attribute;

import net.minecraft.resources.*;
import net.minecraft.world.entity.ai.attributes.*;

public class LodestoneAttributeBuilder {
    public final ResourceLocation id;
    public final double defaultValue;
    public final double minValue;
    public final double maxValue;

    public ResourceLocation baseId;
    public boolean forcePercentage;

    public boolean syncable;
    public Attribute.Sentiment sentiment = Attribute.Sentiment.POSITIVE;

    public LodestoneAttributeBuilder(ResourceLocation id, double defaultValue) {
        this(id, defaultValue, 0, 0);
    }

    public LodestoneAttributeBuilder(ResourceLocation id, double defaultValue, double minValue, double maxValue) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public LodestoneAttributeBuilder setAsBaseAttribute() {
        return setAsBaseAttribute(ResourceLocation.withDefaultNamespace("base_" + id.getPath()));
    }
    public LodestoneAttributeBuilder setAsBaseAttribute(ResourceLocation baseId) {
        this.baseId = baseId;
        return this;
    }

    public LodestoneAttributeBuilder forcePercentageDisplay() {
        this.forcePercentage = true;
        return this;
    }

    public LodestoneAttributeBuilder setSyncable(boolean syncable) {
        this.syncable = syncable;
        return this;
    }

    public LodestoneAttributeBuilder setSentiment(Attribute.Sentiment sentiment) {
        this.sentiment = sentiment;
        return this;
    }

    public Attribute build() {
        if (minValue < maxValue) {
            return new LodestoneRangedAttribute(id, baseId, defaultValue, minValue, maxValue, forcePercentage).setSyncable(syncable).setSentiment(sentiment);
        }
        return new LodestoneAttribute(id, baseId, defaultValue, forcePercentage).setSyncable(syncable).setSentiment(sentiment);
    }
}