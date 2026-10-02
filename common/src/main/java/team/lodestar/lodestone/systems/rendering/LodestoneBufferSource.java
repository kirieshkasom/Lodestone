package team.lodestar.lodestone.systems.rendering;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;
import team.lodestar.lodestone.systems.rendering.rendeertype.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.function.Supplier;

public class LodestoneBufferSource extends MultiBufferSource.BufferSource {
    protected final Supplier<ByteBufferBuilder> bufferSupplier;

    private final Collection<RenderType> additives = new HashSet<>();
    private final Collection<RenderType> nonAdditives = new HashSet<>();

    protected LodestoneBufferSource() {
        this(()->new ByteBufferBuilder(786432), new LinkedHashMap<>());
    }

    protected LodestoneBufferSource(Supplier<ByteBufferBuilder> bufferSupplier, SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
        super(bufferSupplier.get(), fixedBuffers);
        this.bufferSupplier = bufferSupplier;
    }

    public void endBatches(boolean additives) {
        Collection<RenderType> renderTypes = additives ? getAdditives() : getNonAdditives();
        for (RenderType type : renderTypes) {
            endBatch(type);
        }
    }

    public @NotNull VertexConsumer getBuffer(@NotNull LodestoneRenderTypeBuilder renderTypeBuilder) {
        return getBuffer(renderTypeBuilder.getRenderType());
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
        if (!fixedBuffers.containsKey(renderType)) {
            fixedBuffers.put(renderType, bufferSupplier.get());
            if (renderType instanceof LodestoneRenderType lodestoneRenderType) {
                var group = lodestoneRenderType.isAdditive() ? additives : nonAdditives;
                group.add(renderType);
            }
        }
        return super.getBuffer(renderType);
    }

    @Override
    public void endBatch(@NotNull RenderType renderType) {
        LodestoneRenderingSystem.updateUniforms(renderType);
        super.endBatch(renderType);
        LodestoneRenderingSystem.resetUniforms(renderType);
    }

    public Collection<RenderType> getAdditives() {
        return additives;
    }

    public Collection<RenderType> getNonAdditives() {
        return nonAdditives;
    }
}
