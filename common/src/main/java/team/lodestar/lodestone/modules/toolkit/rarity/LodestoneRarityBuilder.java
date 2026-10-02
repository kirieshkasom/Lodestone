package team.lodestar.lodestone.modules.toolkit.rarity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.awt.*;
import java.util.function.UnaryOperator;

/**
 * This builder is used to define and format custom rarities.
 * It returns an EnumProxy which is required for adding a new Rarity entry to the enum through NeoForge's Extensible Enum System.
 */
public class LodestoneRarityBuilder {
    private int id = -1;
    private TextColor color;
    private boolean italic;
    private boolean bold;
    private boolean obfuscated;
    private boolean underlined;
    private boolean strikethrough;
    private final String name;

    public LodestoneRarityBuilder(ResourceLocation name) {
        this.name = name.toString();
    }

    public LodestoneRarityBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public LodestoneRarityBuilder withColor(TextColor color) {
        this.color = color;
        return this;
    }

    public LodestoneRarityBuilder withColor(ChatFormatting color) {
        this.color = TextColor.fromLegacyFormat(color);
        return this;
    }

    public LodestoneRarityBuilder withColor(int color) {
        this.color = TextColor.fromRgb(color);
        return this;
    }

    public LodestoneRarityBuilder withColor(Color color) {
        this.color = TextColor.fromRgb(color.getRGB());
        return this;
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

    public EnumProxy<Rarity> build() {
        return new EnumProxy<>(Rarity.class, this.id, this.name, (UnaryOperator<Style>) s -> s
                .withColor(this.color).withItalic(this.italic).withBold(this.bold)
                .withObfuscated(this.obfuscated).withUnderlined(this.underlined)
                .withStrikethrough(this.strikethrough)
        );
    }
}
