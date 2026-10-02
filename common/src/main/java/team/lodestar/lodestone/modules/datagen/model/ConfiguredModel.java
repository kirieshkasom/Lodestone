package team.lodestar.lodestone.modules.datagen.model;

import net.minecraft.resources.ResourceLocation;

public final class ConfiguredModel {
    private final ModelFile model;
    private final int x;
    private final int y;
    private final boolean uvLock;
    private final int weight;

    private ConfiguredModel(ModelFile model, int x, int y, boolean uvLock, int weight) {
        this.model = model;
        this.x = x;
        this.y = y;
        this.uvLock = uvLock;
        this.weight = weight;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ModelFile model() {
        return model;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public boolean uvLock() {
        return uvLock;
    }

    public int weight() {
        return weight;
    }

    public static final class Builder {
        private ModelFile model;
        private int x;
        private int y;
        private boolean uvLock;
        private int weight = 1;

        public Builder modelFile(ModelFile model) {
            if (model == null) {
                throw new NullPointerException("Model cannot be null");
            }
            this.model = model;
            return this;
        }

        public Builder rotationX(int x) {
            validateRotation(x);
            this.x = x;
            return this;
        }

        public Builder rotationY(int y) {
            validateRotation(y);
            this.y = y;
            return this;
        }

        public Builder uvLock(boolean uvLock) {
            this.uvLock = uvLock;
            return this;
        }

        public Builder weight(int weight) {
            if (weight < 1) {
                throw new IllegalArgumentException("Model weight must be at least one");
            }
            this.weight = weight;
            return this;
        }

        public ConfiguredModel build() {
            if (model == null) {
                throw new NullPointerException("Model cannot be null");
            }
            return new ConfiguredModel(model, x, y, uvLock, weight);
        }

        private void validateRotation(int rotation) {
            if (rotation % 90 != 0) {
                throw new IllegalArgumentException("Model rotation must be a multiple of 90 degrees");
            }
        }
    }
}
