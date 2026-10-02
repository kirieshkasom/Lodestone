package team.lodestar.lodestone.internal.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Function;

public final class ParticleModelAccess {
    private static Function<BakedModel, TextureAtlasSprite> itemSprite = BakedModel::getParticleIcon;
    private static BreakingParticleTint tint = (state, level, pos) -> !state.is(Blocks.GRASS_BLOCK);

    private ParticleModelAccess() {
    }

    public static void install(Function<BakedModel, TextureAtlasSprite> spriteProvider, BreakingParticleTint tintProvider) {
        itemSprite = Objects.requireNonNull(spriteProvider);
        tint = Objects.requireNonNull(tintProvider);
    }

    public static TextureAtlasSprite itemSprite(BakedModel model) {
        return itemSprite.apply(model);
    }

    public static boolean isTinted(BlockState state, ClientLevel level, BlockPos pos) {
        return tint.isTinted(state, level, pos);
    }

    @FunctionalInterface
    public interface BreakingParticleTint {
        boolean isTinted(BlockState state, ClientLevel level, BlockPos pos);
    }
}
