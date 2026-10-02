package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventAttachment;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class FabricWorldEventStorage implements WorldEventStorage {
    private static final String DATA_NAME = "lodestone_world_events";
    private static final SavedData.Factory<FabricWorldEventData> FACTORY = new SavedData.Factory<>(FabricWorldEventData::new, FabricWorldEventData::load, null);
    private final Map<Level, WorldEventAttachment> clientAttachments = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public WorldEventAttachment get(Level level) {
        if (level.isClientSide) {
            return clientAttachments.computeIfAbsent(level, ignored -> new WorldEventAttachment());
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalArgumentException("Server world event storage requires a ServerLevel");
        }
        FabricWorldEventData data = serverLevel.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
        data.setDirty();
        return data.attachment;
    }

    public void clearClient() {
        clientAttachments.clear();
    }

    private static final class FabricWorldEventData extends SavedData {
        private final WorldEventAttachment attachment;

        private FabricWorldEventData() {
            this(new WorldEventAttachment());
        }

        private FabricWorldEventData(WorldEventAttachment attachment) {
            this.attachment = attachment;
        }

        private static FabricWorldEventData load(CompoundTag tag, HolderLookup.Provider registries) {
            WorldEventAttachment attachment = new WorldEventAttachment();
            attachment.deserializeNBT(registries, tag);
            return new FabricWorldEventData(attachment);
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            return attachment.serializeNBT(registries);
        }
    }
}
