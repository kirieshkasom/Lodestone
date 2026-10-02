package team.lodestar.lodestone.modules.toolkit.worldevent;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.worldevent.LodestoneWorldEventRegistry;

import java.util.ArrayList;

public class WorldEventAttachment {

    public final ArrayList<WorldEventInstance> activeWorldEvents = new ArrayList<>();
    public final ArrayList<WorldEventInstance> inboundWorldEvents = new ArrayList<>();

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        CompoundTag worldTag = new CompoundTag();
        worldTag.putInt("worldEventCount", activeWorldEvents.size());
        for (int i = 0; i < activeWorldEvents.size(); i++) {
            WorldEventInstance instance = activeWorldEvents.get(i);
            CompoundTag instanceTag = instance.serializeNBT();
            worldTag.put("worldEvent_" + i, instanceTag);
        }
        tag.put("worldEventData", worldTag);
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        activeWorldEvents.clear();
        CompoundTag worldTag = tag.getCompound("worldEventData");
        int worldEventCount = worldTag.getInt("worldEventCount");
        for (int i = 0; i < worldEventCount; i++) {
            CompoundTag instanceTag = worldTag.getCompound("worldEvent_" + i);
            WorldEventType type = LodestoneWorldEventRegistry.registry().get(ResourceLocation.parse(instanceTag.getString("type")));
            WorldEventInstance eventInstance = type.createInstance(instanceTag);
            activeWorldEvents.add(eventInstance);
        }
    }
}
