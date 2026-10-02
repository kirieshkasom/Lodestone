package team.lodestar.lodestone.modules.toolkit.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import team.lodestar.lodestone.LodestoneLib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LodestoneCodecs {

    public static Codec<BlockPos> BLOCK_POS = stringifyCodec(str -> {
        String[] parts = str.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid BlockPos string format: " + str);
        }
        return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }, pos -> String.format("%s;%s;%s", pos.getX(), pos.getY(), pos.getZ()));


    public static <T> Codec<T> stringifyCodec(Function<String, T> fromString, Function<T, String> toString) {
        return Codec.STRING.comapFlatMap(str -> {
            try {
                return DataResult.success(fromString.apply(str));
            } catch (Exception e) {
                return DataResult.error(() -> "Failed to parse string: " + str + " - " + e.getMessage());
            }
        }, toString);
    }

    public static <O> Codec<Optional<O>> optionalCodec(Codec<O> codec) {
        return Codec.either(
                codec,
                Codec.unit(Unit.INSTANCE)
        ).xmap(
                e -> e.map(Optional::of, u -> Optional.empty()),
                o -> o.map(Either::<O, Unit>left).orElse(Either.right(Unit.INSTANCE))
        );
    }
}