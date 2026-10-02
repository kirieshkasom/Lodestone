package team.lodestar.lodestone.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import team.lodestar.lodestone.internal.worldevent.WorldEventRenderCallback;

public final class FabricWorldEventRenderEvents {
    public static final Event<WorldEventRenderCallback> EVENT = EventFactory.createArrayBacked(WorldEventRenderCallback.class, callbacks -> (instance, renderer, poseStack, multiBufferSource, partialTicks) -> {
        for (WorldEventRenderCallback callback : callbacks) {
            callback.render(instance, renderer, poseStack, multiBufferSource, partialTicks);
        }
    });

    private FabricWorldEventRenderEvents() {
    }
}
