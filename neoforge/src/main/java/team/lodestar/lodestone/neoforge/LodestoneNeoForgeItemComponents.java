package team.lodestar.lodestone.neoforge;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.registration.LodestoneItemComponents;

public final class LodestoneNeoForgeItemComponents {
    private LodestoneNeoForgeItemComponents() {
    }

    public static void register(IEventBus modEventBus) {
        DeferredRegister<DataComponentType<?>> components = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, LodestoneCommon.LODESTONE);
        LodestoneItemComponents.register(new NeoForgeRegistryRegistrar<>(components));
        components.register(modEventBus);
    }
}
