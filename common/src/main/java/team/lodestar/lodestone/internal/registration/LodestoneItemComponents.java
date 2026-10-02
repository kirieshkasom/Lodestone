package team.lodestar.lodestone.internal.registration;

import net.minecraft.core.component.DataComponentType;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.toolkit.rarity.LodestoneRarity;

public final class LodestoneItemComponents {
    public static final RegistryEntry<DataComponentType<LodestoneRarity>> RARITY_STYLE = new RegistryEntry<>(
            LodestoneCommon.lodestonePath("rarity_style"),
            () -> DataComponentType.<LodestoneRarity>builder()
                    .persistent(LodestoneRarity.CODEC)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.fromCodec(LodestoneRarity.CODEC))
                    .build());

    private LodestoneItemComponents() {
    }

    public static void register(RegistryRegistrar<DataComponentType<?>> registrar) {
        registrar.register(RARITY_STYLE);
    }
}
