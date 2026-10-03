package team.lodestar.lodestone.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.registry.common.LodestoneWorldEventTypes;

import net.minecraft.world.item.Item;

public final class FabricTestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        TestContent.ARMOR_MATERIAL = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL,
                TestContent.id("crown"), TestContent.createArmorMaterial());
        TestContent.EVENT = () -> LodestoneWorldEventTypes.WORLD_EVENT_TYPE_REGISTRY.get(TestContent.id("beacon"));
        Registry.register(LodestoneWorldEventTypes.WORLD_EVENT_TYPE_REGISTRY, TestContent.id("beacon"), TestContent.createEventType());
        TestContent.HELMET = () -> BuiltInRegistries.ITEM.get(TestContent.id("crown"));
        Registry.register(BuiltInRegistries.ITEM, TestContent.id("crown"), TestContent.createHelmet());
        TestContent.PERSPECTIVE_PROBE = () -> BuiltInRegistries.ITEM.get(TestContent.id("perspective_probe"));
        TestContent.LAYER_PROBE = () -> BuiltInRegistries.ITEM.get(TestContent.id("layer_probe"));
        Registry.register(BuiltInRegistries.ITEM, TestContent.id("perspective_probe"), new Item(new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, TestContent.id("layer_probe"), new Item(new Item.Properties()));
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> TestServerCommands.register(dispatcher));
    }
}
