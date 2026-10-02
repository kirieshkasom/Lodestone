package team.lodestar.lodestone.modules.datagen.implementation;

import net.minecraft.data.PackOutput;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.datagen.providers.tag.LodestoneBlockTagsSystem;

public final class LodestoneBlockTagDatagen extends LodestoneBlockTagsSystem {
    public LodestoneBlockTagDatagen(PackOutput output) {
        super(output, LodestoneCommon.LODESTONE);
    }
}
