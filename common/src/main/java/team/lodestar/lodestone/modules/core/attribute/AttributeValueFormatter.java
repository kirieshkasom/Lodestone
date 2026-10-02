package team.lodestar.lodestone.modules.core.attribute;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

final class AttributeValueFormatter {
    private AttributeValueFormatter() {
    }

    static MutableComponent format(AttributeModifier.Operation operation, double value, boolean forcePercentage) {
        boolean percentage = forcePercentage || (operation != null && operation != AttributeModifier.Operation.ADD_VALUE);
        double displayValue = percentage ? value * 100.0 : value;
        DecimalFormat formatter = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
        String text = formatter.format(displayValue);
        return Component.literal(percentage ? text + "%" : text);
    }
}
