package team.lodestar.lodestone.helpers;

import net.minecraft.core.*;
import net.minecraft.nbt.*;

import java.util.*;

import static team.lodestar.lodestone.LodestoneLib.LOGGER;

public class NBTHelper {

    public static BlockPos readBlockPos(CompoundTag tag) {
        return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag.get("position")).resultOrPartial(LOGGER::error).orElse(null);
    }

    public static BlockPos readBlockPos(ListTag tag, int index) {
        return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag.get(index)).resultOrPartial(LOGGER::error).orElse(null);
    }

    public static CompoundTag saveBlockPos(BlockPos pos) {
        return saveBlockPos(new CompoundTag(), pos);
    }

    public static CompoundTag saveBlockPos(CompoundTag tag, BlockPos pos) {
        BlockPos.CODEC
                .encodeStart(NbtOps.INSTANCE, pos)
                .resultOrPartial(LOGGER::error)
                .ifPresent(p -> tag.put("position", p));
        return tag;
    }

    public static ListTag saveBlockPosToList(BlockPos pos) {
        return saveBlockPosToList(new ListTag(), pos);
    }

    public static ListTag saveBlockPosToList(ListTag tag, BlockPos pos) {
        BlockPos.CODEC
                .encodeStart(NbtOps.INSTANCE, pos)
                .resultOrPartial(LOGGER::error)
                .ifPresent(tag::add);
        return tag;
    }
}