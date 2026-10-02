package team.lodestar.lodestone.modules.toolkit.rarity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.awt.Color;
import java.util.Optional;

public class LodestoneRarityBuilder {
    private int legacyId = -1;
    private Optional<Integer> color = Optional.empty();
    private boolean italic;
    private boolean bold;
    private boolean obfuscated;
    private boolean underlined;
    private boolean strikethrough;
    private final ResourceLocation id;

    public LodestoneRarityBuilder(ResourceLocation id) {
        this.id = id;
    }

    public LodestoneRarityBuilder withId(int id) {
        this.legacyId = id;
        return this;
    }

    public LodestoneRarityBuilder withColor(TextColor color) {
        this.color = color == null ? Optional.empty() : Optional.of(color.getValue());
        return this;
    }

    public LodestoneRarityBuilder withColor(ChatFormatting color) {
        Integer colorValue = color == null ? null : color.getColor();
        this.color = colorValue == null ? Optional.empty() : Optional.of(colorValue);
        return this;
    }

    public LodestoneRarityBuilder withColor(int color) {
        this.color = Optional.of(color & 0xFFFFFF);
        return this;
    }

    public LodestoneRarityBuilder withColor(Color color) {
        return withColor(color.getRGB());
    }

    public LodestoneRarityBuilder setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    public LodestoneRarityBuilder setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public LodestoneRarityBuilder setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    public LodestoneRarityBuilder setUnderlined(boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    public LodestoneRarityBuilder setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public int getLegacyId() {
        return this.legacyId;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public LodestoneRarity build() {
        return new LodestoneRarity(this.id, this.color, this.italic, this.bold, this.obfuscated, this.underlined, this.strikethrough);
    }
}
