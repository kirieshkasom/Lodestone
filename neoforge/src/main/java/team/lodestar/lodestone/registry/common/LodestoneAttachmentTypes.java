package team.lodestar.lodestone.registry.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventAttachment;

import java.util.function.Supplier;

public class LodestoneAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LodestoneCommon.LODESTONE);

    public static final Supplier<AttachmentType<WorldEventAttachment>> WORLD_EVENT_DATA = ATTACHMENT_TYPES.register(
            "world_event_data", () -> AttachmentType.<WorldEventAttachment>builder(WorldEventAttachment::new)
                    .serialize(new IAttachmentSerializer<CompoundTag, WorldEventAttachment>() {
                        @Override
                        public WorldEventAttachment read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                            WorldEventAttachment attachment = new WorldEventAttachment();
                            attachment.deserializeNBT(provider, tag);
                            return attachment;
                        }

                        @Override
                        public CompoundTag write(WorldEventAttachment attachment, HolderLookup.Provider provider) {
                            return attachment.serializeNBT(provider);
                        }
                    })
                    .build()
    );
}
