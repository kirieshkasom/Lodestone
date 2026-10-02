package team.lodestar.lodestone.registry.client;

import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.systems.asset.ReloadListener;
import team.lodestar.lodestone.modules.rendering.model.IRenderableModel;
import team.lodestar.lodestone.modules.rendering.model.obj.ObjModel;

import java.util.ArrayList;
import java.util.List;

public class LodestoneModels {
    public static List<IRenderableModel> MODELS = new ArrayList<>();
    private static final ReloadListener reloadListener = new ReloadListener(LodestoneModels::loadModels);

    public static final ObjModel SUZANNE = register(ObjModel.Builder.of(LodestoneCommon.lodestonePath("models/suzanne.obj"))
            .build()
    );
    
    public static <T extends IRenderableModel> T register(T model) {
        MODELS.add(model);
        return model;
    }

    public static void loadModels() {
        MODELS.forEach(IRenderableModel::loadModel);
    }

    public static ReloadListener reloadListener() {
        return reloadListener;
    }

    public static void cleanup() {
        MODELS.forEach(IRenderableModel::cleanup);
    }
}
