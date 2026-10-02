package team.lodestar.lodestone.test;

import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import team.lodestar.lodestone.neoforge.rendering.NeoForgeLodestoneArmorClientItemExtensions;

import java.util.function.Consumer;

public final class NeoForgeTestArmorItem extends ArmorItem {
    public NeoForgeTestArmorItem() {
        super(TestContent.ARMOR_MATERIAL, Type.HELMET, new Properties().durability(200));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new NeoForgeLodestoneArmorClientItemExtensions(TestClient.ARMOR));
    }
}
