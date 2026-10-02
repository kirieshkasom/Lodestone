package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance;

import java.util.ArrayList;
import java.util.List;

public record InstanceFormat(List<InstanceElement> elements, int totalFloats, int strideBytes) {


    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private final List<InstanceElement> elements = new ArrayList<>();
        private int totalFloats = 0;

        public Builder add(InstanceWriter... writers) {
            for (InstanceWriter writer : writers) {
                elements.add(new InstanceElement(writer));
                totalFloats += writer.floatCount();
            }
            return this;
        }

        public InstanceFormat build() {
            return new InstanceFormat(List.copyOf(elements), totalFloats, totalFloats * Float.BYTES);
        }
    }
}