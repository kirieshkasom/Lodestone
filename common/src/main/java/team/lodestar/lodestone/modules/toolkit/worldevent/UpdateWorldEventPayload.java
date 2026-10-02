package team.lodestar.lodestone.modules.toolkit.worldevent;

import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import team.lodestar.lodestone.internal.network.PayloadContext;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;

import java.util.UUID;

public class UpdateWorldEventPayload extends OneSidedPayloadData {

    private final UUID uuid;
    private final CompoundTag eventData;

    public UpdateWorldEventPayload(RegistryFriendlyByteBuf byteBuf) {
        this(byteBuf.readUUID(), byteBuf.readNbt());
    }

    public UpdateWorldEventPayload(WorldEventInstance instance) {
        this(instance.uuid, instance.serializeNBT());
    }

    public UpdateWorldEventPayload(UUID uuid, CompoundTag eventData) {
        this.uuid = uuid;
        this.eventData = eventData;
    }

    @Override
    public void handle(PayloadContext context) {
        context.execute(() -> {
            Level level = context.player().level();
            if (level != null) {
                WorldEventAttachment worldData = WorldEventStorageAccess.get(level);
                for (WorldEventInstance instance : worldData.activeWorldEvents) {
                    if (instance.uuid.equals(uuid)) {
                        instance.deserializeNBT(eventData);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void serialize(RegistryFriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeNbt(eventData);
    }
}
