package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.world.level.Level;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventAttachment;
import team.lodestar.lodestone.registry.common.LodestoneAttachmentTypes;

public final class NeoForgeWorldEventStorage implements WorldEventStorage {
    @Override
    public WorldEventAttachment get(Level level) {
        return level.getData(LodestoneAttachmentTypes.WORLD_EVENT_DATA);
    }
}
