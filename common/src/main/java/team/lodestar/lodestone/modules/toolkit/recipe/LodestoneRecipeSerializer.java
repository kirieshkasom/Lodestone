package team.lodestar.lodestone.modules.toolkit.recipe;

import com.mojang.serialization.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.crafting.*;

/**
 * A Recipe Serializer implementation for use with any custom recipe type.
 */
public class LodestoneRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
    public final MapCodec<T> codec;
    public final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public LodestoneRecipeSerializer(MapCodec<T> codec) {
        this.codec = codec;
        this.streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }
}