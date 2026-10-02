package team.lodestar.lodestone.modules.rendering.handlers;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.modules.rendering.model.IRenderableModel;
import team.lodestar.lodestone.modules.rendering.model.geo.BedrockGeometryModel;
import team.lodestar.lodestone.modules.rendering.model.obj.ObjModel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = LodestoneLib.LODESTONE, value = Dist.CLIENT)
public class ModelHandler {
    private static final Map<ResourceLocation, IRenderableModel> MODELS = new HashMap<>();
    private static boolean initializedClient = false;

    public static IRenderableModel register(ResourceLocation location) {
        if (MODELS.containsKey(location)) {
            return MODELS.get(location);
        }

        String fileExtension = location.getPath().substring(location.getPath().lastIndexOf("."));
        IRenderableModel model = switch (fileExtension) {
            case ".obj" -> new ObjModel(location);
            case ".geo" -> new BedrockGeometryModel(location);
            default -> throw new IllegalArgumentException("Unsupported model format: " + fileExtension);
        };

        MODELS.put(location, model);
        if (initializedClient) {
            model.loadModel();
        }
        return model;
    }

    public static <T extends IRenderableModel> T register(ResourceLocation location, T model) {
        if (MODELS.containsKey(location)) {
            MODELS.get(location).cleanup();
        }

        MODELS.put(location, model);
        if (initializedClient) {
            model.loadModel();
        }
        return model;
    }

    public static boolean isRegistered(ResourceLocation location) {
        return MODELS.containsKey(location);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends IRenderableModel> T get(ResourceLocation location, Class<T> type) {
        IRenderableModel model = MODELS.get(location);

        if (model == null) {
            return null;
        }

        if (!type.isInstance(model)) {
            throw new IllegalStateException("Model at " + location + " is not of type " + type.getName());
        }

        return (T) model;
    }

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {
        initializedClient = true;
        for (IRenderableModel model : MODELS.values()) {
            model.loadModel();
        }
    }

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        for (IRenderableModel model : MODELS.values()) {
            model.loadModel();
        }
    }

    @SubscribeEvent
    public static void shutdownEvent(GameShuttingDownEvent event) {
        for (IRenderableModel model : MODELS.values()) {
            model.cleanup();
        }
    }
}
