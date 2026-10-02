package team.lodestar.lodestone.neoforge;

import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import team.lodestar.lodestone.modules.toolkit.rarity.LodestoneRarityBuilder;

public final class LodestoneNeoForgeRarityAdapter {
    private LodestoneNeoForgeRarityAdapter() {
    }

    public static EnumProxy<Rarity> buildEnumProxy(LodestoneRarityBuilder builder) {
        return new EnumProxy<>(Rarity.class, builder.getLegacyId(), builder.getId().toString(), builder.build().style());
    }
}
