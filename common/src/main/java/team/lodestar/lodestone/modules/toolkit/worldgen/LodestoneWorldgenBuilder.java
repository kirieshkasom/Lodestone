package team.lodestar.lodestone.modules.toolkit.worldgen;

import net.minecraft.core.*;
import net.minecraft.world.level.*;

import java.util.*;

public class LodestoneWorldgenBuilder {

    public static LodestoneWorldgenBuilder create() {
        return new LodestoneWorldgenBuilder();
    }

    protected final ArrayList<LodestoneWorldgenBuilderLayer> layers = new ArrayList<>();

    protected PlacementCondition defaultPlacementCondition = (level, entry) -> true;
    protected AdditionalPlacement defaultAdditionalPlacement = (level, entry) -> {
    };

    public LodestoneWorldgenBuilder() {
    }

    public LodestoneWorldgenBuilder addAdditionalPlacement(AdditionalPlacement defaultAdditionalPlacement) {
        this.defaultAdditionalPlacement = defaultAdditionalPlacement;
        return this;
    }

    public LodestoneWorldgenBuilder addPlacementCondition(PlacementCondition defaultPlacementCondition) {
        this.defaultPlacementCondition = defaultPlacementCondition;
        return this;
    }

    public LodestoneWorldgenBuilderLayer createLayer() {
        LodestoneWorldgenBuilderLayer layer = new LodestoneWorldgenBuilderLayer();
        layer.addAdditionalPlacement(defaultAdditionalPlacement);
        layer.addPlacementCondition(defaultPlacementCondition);
        layers.add(layer);
        return layer;
    }

    public ArrayList<LodestoneWorldgenBuilderLayer> getLayers() {
        return layers;
    }

    public LodestoneWorldgenBuilderLayer getLayer(int index) {
        return layers.get(index);
    }


    public ArrayList<BlockPos> getAffectedArea(int layerIndex) {
        return new ArrayList<>(getLayer(layerIndex).getAffectedArea());
    }

    public ArrayList<BlockPos> getAffectedArea() {
        ArrayList<BlockPos> affectedArea = new ArrayList<>();
        for (LodestoneWorldgenBuilderLayer layer : getLayers()) {
            affectedArea.addAll(layer.getAffectedArea());
        }
        return affectedArea;
    }

    public Collection<LodestoneWorldgenBuilderEntry> getEntries(int layerIndex) {
        return getLayer(layerIndex).getEntries();
    }

    public ArrayList<LodestoneWorldgenBuilderEntry> getAllEntries() {
        ArrayList<LodestoneWorldgenBuilderEntry> entries = new ArrayList<>();
        for (LodestoneWorldgenBuilderLayer layer : getLayers()) {
            entries.addAll(layer.getEntries());
        }
        return entries;
    }

    public Collection<LodestoneWorldgenBuilderEntry> getOrderedEntries(int layerIndex) {
        return getLayer(layerIndex).getOrderedEntries();
    }

    public ArrayList<LodestoneWorldgenBuilderEntry> getOrderedEntries() {
        ArrayList<LodestoneWorldgenBuilderEntry> entries = new ArrayList<>();
        for (LodestoneWorldgenBuilderLayer layer : getLayers()) {
            entries.addAll(layer.getOrderedEntries());
        }
        return entries;
    }

    public LodestoneWorldgenBuilder merge(LodestoneWorldgenBuilder other) {
        getLayers().addAll(other.getLayers());
        return this;
    }

    public void place(WorldGenLevel level) {
        Set<BlockPos> skippedPositions = new HashSet<>();
        for (LodestoneWorldgenBuilderLayer layer : getLayers()) {
            for (LodestoneWorldgenBuilderEntry entry : layer.getOrderedEntries()) {
                if (!entry.isImportant() && skippedPositions.contains(entry.position())) {
                    continue;
                }
                if (entry.tryPlace(level)) {
                    skippedPositions.add(entry.position());
                }
            }
        }
    }
}