package team.lodestar.lodestone.modules.rendering.model.obj.modifier;

import team.lodestar.lodestone.modules.rendering.model.obj.IndexedModel;
import team.lodestar.lodestone.modules.rendering.model.obj.ObjParser;
import team.lodestar.lodestone.modules.rendering.model.obj.data.IndexedMesh;

public abstract class ModelModifier {

    /**
     * Apply the modification to the model before it is built.
     * @param parsedModel The model that is being built.
     */
    public void applyEarly(ObjParser.Builder parsedModel) {

    }

    /**
     * Apply the modification to the whole {@link IndexedModel}.
     * @param model The model to apply the modification to.
     */
    public abstract void apply(IndexedModel model);

    /**
     * Apply the modification to a specific {@link IndexedMesh} in an {@link IndexedModel}.
     * @param model The model that contains the mesh.
     * @param mesh The mesh to apply the modification to.
     */
    public abstract void apply(IndexedModel model, IndexedMesh mesh);
}
