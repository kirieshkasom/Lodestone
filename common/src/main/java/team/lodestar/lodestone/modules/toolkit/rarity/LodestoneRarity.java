package team.lodestar.lodestone.modules.toolkit.rarity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import team.lodestar.lodestone.internal.registration.LodestoneItemComponents;

import java.util.Optional;
import java.util.function.UnaryOperator;

public record LodestoneRarity(ResourceLocation id, Rarity vanillaRarity, Optional<Integer> color, boolean italic, boolean bold, boolean obfuscated, boolean underlined, boolean strikethrough) {
    public static final Codec<LodestoneRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(LodestoneRarity::id),
            Rarity.CODEC.optionalFieldOf("vanilla_rarity", Rarity.COMMON).forGetter(LodestoneRarity::vanillaRarity),
            Codec.INT.optionalFieldOf("color").forGetter(LodestoneRarity::color),
            Codec.BOOL.optionalFieldOf("italic", false).forGetter(LodestoneRarity::italic),
            Codec.BOOL.optionalFieldOf("bold", false).forGetter(LodestoneRarity::bold),
            Codec.BOOL.optionalFieldOf("obfuscated", false).forGetter(LodestoneRarity::obfuscated),
            Codec.BOOL.optionalFieldOf("underlined", false).forGetter(LodestoneRarity::underlined),
            Codec.BOOL.optionalFieldOf("strikethrough", false).forGetter(LodestoneRarity::strikethrough)
    ).apply(instance, LodestoneRarity::new));

    /**
     * Returns the custom rarity identity and style independently of the vanilla rarity tier.
     * Vanilla rarity queries retain Minecraft's enchantment promotion rules.
     */
    public static Optional<LodestoneRarity> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(LodestoneItemComponents.RARITY_STYLE.get()));
    }

    public LodestoneRarity(ResourceLocation id, Optional<Integer> color, boolean italic, boolean bold, boolean obfuscated, boolean underlined, boolean strikethrough) {
        this(id, Rarity.COMMON, color, italic, bold, obfuscated, underlined, strikethrough);
    }

    public UnaryOperator<Style> style() {
        return style -> {
            Style result = style;
            if (this.color.isPresent()) {
                result = result.withColor(TextColor.fromRgb(this.color.get()));
            }
            return result.withItalic(this.italic).withBold(this.bold).withObfuscated(this.obfuscated)
                    .withUnderlined(this.underlined).withStrikethrough(this.strikethrough);
        };
    }
}
