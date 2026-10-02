package team.lodestar.lodestone.test;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.registry.common.LodestoneWorldEventTypes;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

@Mod(TestContent.MOD_ID)
public final class NeoForgeTestMod {
    public NeoForgeTestMod(IEventBus modBus) {
        DeferredRegister<WorldEventType> events = DeferredRegister.create(LodestoneWorldEventTypes.WORLD_EVENT_TYPE_KEY, TestContent.MOD_ID);
        DeferredRegister<Item> items = DeferredRegister.create(Registries.ITEM, TestContent.MOD_ID);
        DeferredRegister<ArmorMaterial> materials = DeferredRegister.create(Registries.ARMOR_MATERIAL, TestContent.MOD_ID);
        TestContent.ARMOR_MATERIAL = materials.register("crown", TestContent::createArmorMaterial);
        TestContent.EVENT = events.register("beacon", TestContent::createEventType);
        TestContent.HELMET = items.register("crown", NeoForgeTestArmorItem::new);
        events.register(modBus);
        materials.register(modBus);
        items.register(modBus);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> TestServerCommands.register(event.getDispatcher()));
    }
}
