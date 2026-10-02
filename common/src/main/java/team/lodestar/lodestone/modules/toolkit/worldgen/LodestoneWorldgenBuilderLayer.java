package team.lodestar.lodestone.modules.toolkit.worldgen;

import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;

import java.util.*;

public class LodestoneWorldgenBuilderLayer {

    protected final HashMap<BlockPos, LodestoneWorldgenBuilderEntry> entries = new HashMap<>();
    protected final ArrayList<BlockPos> entryOrder = new ArrayList<>();
    protected PlacementCondition defaultPlacementCondition;
    protected AdditionalPlacement defaultAdditionalPlacement;

    public LodestoneWorldgenBuilderLayer() {
    }

    public ArrayList<BlockPos> getAffectedArea() {
        return new ArrayList<>(entries.keySet());
    }

    public LodestoneWorldgenBuilderLayer addAdditionalPlacement(AdditionalPlacement defaultAdditionalPlacement) {
        this.defaultAdditionalPlacement = defaultAdditionalPlacement;
        return this;
    }

    public LodestoneWorldgenBuilderLayer addPlacementCondition(PlacementCondition defaultPlacementCondition) {
        this.defaultPlacementCondition = defaultPlacementCondition;
        return this;
    }

    public LodestoneWorldgenBuilderLayer merge(LodestoneWorldgenBuilderLayer other) {
        this.entries.putAll(other.entries);
        return this;
    }

    public LodestoneWorldgenBuilderEntry add(BlockPos blockPos, Block block) {
        return add(blockPos, block.defaultBlockState());
    }

    public LodestoneWorldgenBuilderEntry add(BlockPos pos, BlockState state) {
        if (pos instanceof BlockPos.MutableBlockPos mutable) {
            pos = mutable.immutable();
        }
        LodestoneWorldgenBuilderEntry entry = new LodestoneWorldgenBuilderEntry(pos, state);
        if (!entry.hasPlacementCondition()) {
            entry.addPlacementCondition(defaultPlacementCondition);
        }
        if (!entry.hasAdditionalPlacement()) {
            entry.addAdditionalPlacement(defaultAdditionalPlacement);
        }
        add(pos, entry);
        return entry;
    }

    public LodestoneWorldgenBuilderLayer add(BlockPos pos, LodestoneWorldgenBuilderEntry entry) {
        entries.put(pos, entry);
        entryOrder.add(pos);
        return this;
    }

    public LodestoneWorldgenBuilderLayer remove(BlockPos pos) {
        entries.remove(pos);
        entryOrder.remove(pos);
        return this;
    }

    public LodestoneWorldgenBuilderEntry get(BlockPos pos) {
        return entries.get(pos);
    }

    public boolean containsKey(BlockPos pos) {
        return entries.containsKey(pos);
    }

    public Collection<LodestoneWorldgenBuilderEntry> getEntries() {
        return entries.values();
    }

    public ArrayList<LodestoneWorldgenBuilderEntry> getOrderedEntries() {
        ArrayList<LodestoneWorldgenBuilderEntry> orderedEntries = new ArrayList<>();
        for (BlockPos pos : entryOrder) {
            orderedEntries.add(entries.get(pos));
        }
        return orderedEntries;
    }

    public ArrayList<LodestoneWorldgenBuilderEntry> getRandomEntries(RandomSource random, int amount) {
        ArrayList<LodestoneWorldgenBuilderEntry> randomEntries = new ArrayList<>();
        List<BlockPos> keys = new ArrayList<>(entries.keySet());
        shuffle(random, keys);
        for (int i = 0; i < Math.min(amount, keys.size()); i++) {
            randomEntries.add(entries.get(keys.get(i)));
        }
        return randomEntries;
    }

    public static <T> List<T> shuffle(RandomSource random, Collection<T> collection) {
        return collection.stream().sorted((a, b) -> random.nextIntBetweenInclusive(-1, 0)).toList();
    }
}