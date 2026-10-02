package team.lodestar.lodestone.modules.toolkit.worldevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import team.lodestar.lodestone.registry.client.LodestoneWorldEventRenderers;
import team.lodestar.lodestone.modules.toolkit.command.CommandCodec;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class WorldEventType {
    public final ResourceLocation id;
    public final EventInstanceSupplier<?> supplier;
    public final boolean clientSynced;
    public final @Nullable CommandCodec<? extends WorldEventInstance> commandCodec;

    /**
     * Creates a new world event type.
     * @param id The id of the event type
     * @param supplier The supplier for the event instance
     * @param clientSynced Should this event exist on the client? It will be automatically synced upon creation of the event in {@link WorldEventInstance#sync(net.minecraft.world.level.Level)}
     */
    public WorldEventType(ResourceLocation id, EventInstanceSupplier<?> supplier, boolean clientSynced, @Nullable CommandCodec<? extends WorldEventInstance> commandCodec) {
        this.id = id;
        this.supplier = supplier;
        this.clientSynced = clientSynced;
        this.commandCodec = commandCodec;
    }

    /**
     * Creates a new world event type.
     * <p>By default, the event will not be client-synced.</p>
     * <p>See {@link #WorldEventType(ResourceLocation, EventInstanceSupplier, boolean, CommandCodec)} for more information.</p>
     * @param id The id of the event type
     * @param supplier The supplier for the event instance
     */
    public WorldEventType(ResourceLocation id, EventInstanceSupplier<?> supplier) {
        this(id, supplier, false, null);
    }

    public boolean isClientSynced() {
        return clientSynced;
    }

    public WorldEventInstance createInstance(CompoundTag tag) {
        return supplier.getInstance().deserializeNBT(tag);
    }

    public static class Builder<T extends WorldEventInstance> {
        private final ResourceLocation id;
        private final EventInstanceSupplier<T> supplier;
        private boolean clientSynced;
        private @Nullable Supplier<WorldEventRenderer<T>> rendererSupplier;
        private @Nullable CommandCodec<? extends WorldEventInstance> commandCodec;

        private Builder(EventInstanceSupplier<T> supplier, ResourceLocation id) {
            this.id = id;
            this.supplier = supplier;
        }

        /**
         * Creates a new world event type.
         *
         * @param supplier The supplier for the event instance.
         * @param id The ID of the event type.
         * @return A new Builder instance for the specified event type.
         */
        public static <T extends WorldEventInstance> Builder<T> of(EventInstanceSupplier<T> supplier, ResourceLocation id) {
            return new Builder<>(supplier, id);
        }

        /**
         * Sets the event to be client-synced and assigns a renderer for the event.
         * The event will be automatically synced upon creation in {@link WorldEventInstance#sync(net.minecraft.world.level.Level)}.
         *
         * @param rendererSupplier The renderer supplier for the event. Set to null if you don't want a renderer for this world event.
         * @return The builder instance with the clientSynced flag set and the renderer assigned.
         */
        public Builder<T> clientSynced(@Nullable Supplier<WorldEventRenderer<T>> rendererSupplier) {
            this.clientSynced = true;
            this.rendererSupplier = rendererSupplier;
            return this;
        }

        /**
         * Sets the event to be client-synced without assigning a renderer.
         * The event will be automatically synced upon creation in {@link WorldEventInstance#sync(net.minecraft.world.level.Level)}.
         *
         * <p>If you want to use a renderer, use {@link #clientSynced(Supplier)} instead.</p>
         *
         * @return The builder instance with the clientSynced flag set and no renderer assigned.
         */
        public Builder<T> clientSynced() {
            return clientSynced(null);
        }

        /**
         * Sets the command codec for the world event type.
         * <p>If you never call this you won't be able to construct world events using commands (/lodestone worldevent create) <p/>
         * @param commandCodec The command codec
         * @return The builder instance with the command codec set.
         */
        public Builder<T> withCommandCodec(CommandCodec<? extends WorldEventInstance> commandCodec) {
            this.commandCodec = commandCodec;
            return this;
        }

        /**
         * Builds the WorldEventType with the specified parameters.
         * Registers the renderer if it is set.
         *
         * @return The built WorldEventType.
         */
        public WorldEventType build() {
            WorldEventType type = new WorldEventType(this.id, this.supplier, this.clientSynced, this.commandCodec);
            if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
                LodestoneWorldEventRenderers.registerRenderer(type, this.rendererSupplier != null ? this.rendererSupplier.get() : null);
            }
            return type;
        }
    }

    public interface EventInstanceSupplier<T extends WorldEventInstance> {
        T getInstance();
    }
}