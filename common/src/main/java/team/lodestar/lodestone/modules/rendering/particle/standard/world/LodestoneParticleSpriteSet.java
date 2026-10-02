package team.lodestar.lodestone.modules.rendering.particle.standard.world;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Supplies indexed particle sprites from the current resource pack, including after resource reloads.
 */
public final class LodestoneParticleSpriteSet implements SpriteSet {

    private final Supplier<List<TextureAtlasSprite>> sprites;

    public LodestoneParticleSpriteSet(Supplier<List<TextureAtlasSprite>> sprites) {
        this.sprites = Objects.requireNonNull(sprites);
    }

    public static LodestoneParticleSpriteSet wrap(SpriteSet sprites) {
        if (sprites instanceof LodestoneParticleSpriteSet indexed) {
            return indexed;
        }
        if (sprites instanceof ParticleEngine.MutableSpriteSet vanilla) {
            return new LodestoneParticleSpriteSet(() -> vanilla.sprites);
        }
        throw new IllegalArgumentException("An indexed sprite supplier is required for this sprite set");
    }

    public int size() {
        return sprites.get().size();
    }

    public TextureAtlasSprite get(int index) {
        List<TextureAtlasSprite> current = sprites.get();
        return current.get(Mth.clamp(index, 0, current.size() - 1));
    }

    @Override
    public TextureAtlasSprite get(int age, int lifetime) {
        List<TextureAtlasSprite> current = sprites.get();
        return current.get(age * (current.size() - 1) / lifetime);
    }

    @Override
    public TextureAtlasSprite get(RandomSource random) {
        List<TextureAtlasSprite> current = sprites.get();
        return current.get(random.nextInt(current.size()));
    }
}
