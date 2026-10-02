package team.lodestar.lodestone.modules.toolkit.worldevent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.internal.network.LodestoneNetworking;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;
import team.lodestar.lodestone.internal.worldevent.WorldEventCallbackAccess;

import java.util.Iterator;

public class WorldEventHandler {

    public static <T extends WorldEventInstance> T addWorldEvent(Level level, T instance) {
        return addWorldEvent(level, true, instance);
    }

    public static <T extends WorldEventInstance> T addWorldEvent(Level level, boolean shouldStart, T instance) {
        WorldEventCallbackAccess.callbacks().creation(instance, level);

        WorldEventAttachment worldData = WorldEventStorageAccess.get(level);

        worldData.inboundWorldEvents.add(instance);
        if (shouldStart) {
            instance.start(level);
        }
        instance.sync(level);

        return instance;
    }

    public static void playerJoin(ServerPlayer player) {
        WorldEventAttachment worldData = WorldEventStorageAccess.get(player.serverLevel());
        for (WorldEventInstance instance : worldData.activeWorldEvents) {
            if (instance.type.isClientSynced()) {
                WorldEventInstance.sync(instance, player);
            }
        }
    }

    public static void worldTick(Level level) {
        if (!level.isClientSide) {
            tick(level);
        }
    }

    /**
     * Ticks all active world events in the given level.
     * <p>
     * Will tick on both client and server side.
     * <p>
     * See {@link WorldEventInstance#tick(Level)}
     */
    public static void tick(Level level) {
        WorldEventAttachment c = WorldEventStorageAccess.get(level);
        c.activeWorldEvents.addAll(c.inboundWorldEvents);
        c.inboundWorldEvents.clear();

        Iterator<WorldEventInstance> iterator = c.activeWorldEvents.iterator();
        while (iterator.hasNext()) {
            WorldEventInstance instance = iterator.next();
            if (instance.discarded) {
                WorldEventCallbackAccess.callbacks().discard(instance, level);
                iterator.remove();
            } else {
                if (!instance.isFrozen()) {
                    WorldEventCallbackAccess.callbacks().tick(instance, level);
                    instance.tick(level);
                }
                if (instance.dirty) {
                    if (!level.isClientSide) {
                        LodestoneNetworking.sendToAllPlayers(new UpdateWorldEventPayload(instance));
                    }
                    instance.dirty = false;
                }
            }
        }
    }
}
