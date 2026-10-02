package team.lodestar.lodestone.modules.rendering.model.obj;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.rendering.model.obj.modifier.ModelModifier;
import team.lodestar.lodestone.modules.rendering.model.obj.modifier.ModifierQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An {@link IndexedModel} that is parsed from an Wavefront OBJ file.
 */
public class ObjModel extends IndexedModel {
    public ObjModel(ResourceLocation modelId) {
        super(modelId);
    }

    @Override
    public void loadModel() {
        ObjParser parser = new ObjParser();
        parser.startParse(this);
        this.applyModifiers();
        this.bakeIndices(VertexFormat.Mode.TRIANGLES, true);
    }

    public void applyEarlyModifiers(ObjParser.Builder parsedModel) {
        if (earlyModifiers != null) {
            earlyModifiers.forEach(modifier -> modifier.applyEarly(parsedModel));
        }
    }

    public static class Builder implements ModifierQueue {
        private final ResourceLocation modelId;
        private VertexFormat.Mode bakeMode;
        private final boolean cacheModifications = false;
        private List<ModelModifier> earlyModifiers;
        private List<ModelModifier> modifiers;

        private Builder(ResourceLocation modelId) {
            this.modelId = modelId;
        }

        public static Builder of(ResourceLocation modelId) {
            return new Builder(modelId);
        }

        public Builder bakeIndicies(VertexFormat.Mode primitiveMode, boolean convertQuadsToTriangles) {
            this.bakeMode = primitiveMode;
            return this;
        }

        public Builder withModifiers(Consumer<ModifierQueue> modifierQueue) {
            this.modifiers = new ArrayList<>();
            this.earlyModifiers = new ArrayList<>();
            modifierQueue.accept(this);
            return this;
        }

        @Override
        public void queueEarlyModifier(ModelModifier modifier) {
            this.earlyModifiers.add(modifier);
        }

        @Override
        public void queueModifier(ModelModifier modifier) {
            this.modifiers.add(modifier);
        }

        public ObjModel build() {
            ObjModel model = new ObjModel(modelId);
            model.earlyModifiers = this.earlyModifiers;
            model.modifiers = this.modifiers;
            return model;
        }

    }
}