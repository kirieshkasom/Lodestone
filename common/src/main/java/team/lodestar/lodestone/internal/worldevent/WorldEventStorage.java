package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.world.level.Level;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventAttachment;

public interface WorldEventStorage {
    WorldEventAttachment get(Level level);
}
