package team.lodestar.lodestone.modules.toolkit.worldevent;

import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.*;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.network.PayloadContext;
import team.lodestar.lodestone.internal.worldevent.LodestoneWorldEventRegistry;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;

public class SyncWorldEventPayload extends OneSidedPayloadData {

    private final ResourceLocation type;
    private final boolean start;
    private final CompoundTag eventData;

    public SyncWorldEventPayload(RegistryFriendlyByteBuf byteBuf) {
        this(byteBuf.readResourceLocation(), byteBuf.readBoolean(), byteBuf.readNbt());
    }

    public SyncWorldEventPayload(WorldEventInstance instance, boolean start) {
        this(instance.type.id, start, instance.serializeNBT());
    }

    public SyncWorldEventPayload(ResourceLocation type, boolean start, CompoundTag eventData) {
        this.type = type;
        this.start = start;
        this.eventData = eventData;
    }

    @Override
    public void handle(PayloadContext context) {
        context.execute(() -> {
            WorldEventType eventType = LodestoneWorldEventRegistry.registry().get(type);
            Level level = context.player().level();
            WorldEventHandler.addWorldEvent(level, start, eventType.createInstance(eventData));
        });
    }

    @Override
    public void serialize(RegistryFriendlyByteBuf byteBuf) {
        byteBuf.writeResourceLocation(type);
        byteBuf.writeBoolean(start);
        byteBuf.writeNbt(eventData);
    }
}
