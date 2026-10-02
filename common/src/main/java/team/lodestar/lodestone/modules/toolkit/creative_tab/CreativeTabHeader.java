package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.network.chat.Component;

public class CreativeTabHeader {

    protected final Component headerText;

    public CreativeTabHeader(CreativeTabCategory category) {
        this.headerText = makeHeaderText(category.getHeaderLangKey());
    }

    protected Component makeHeaderText(String key) {
        return Component.translatable(key);
    }


    public Component getHeaderText() {
        return headerText;
    }
}
