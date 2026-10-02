package team.lodestar.lodestone.internal.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.rendering.handlers.ModelHandler;
import team.lodestar.lodestone.registry.client.LodestoneModels;

import java.util.function.BiConsumer;

public final class LodestoneClientReloads {
    private LodestoneClientReloads() {
    }

    public static void register(BiConsumer<ResourceLocation, PreparableReloadListener> registrar) {
        registrar.accept(LodestoneCommon.lodestonePath("models"), LodestoneModels.reloadListener());
        registrar.accept(LodestoneCommon.lodestonePath("model_handler"), ModelHandler.reloadListener());
    }
}
