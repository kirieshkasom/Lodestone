package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import team.lodestar.lodestone.fabric.mixin.gameplay.CreativeTabPositionMixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class FabricCreativeTabOrdering {
    private static final Set<ResourceLocation> SHARED_TABS = Set.of(ResourceLocation.withDefaultNamespace("hotbar"), ResourceLocation.withDefaultNamespace("search"), ResourceLocation.withDefaultNamespace("inventory"), ResourceLocation.withDefaultNamespace("op_blocks"));

    private FabricCreativeTabOrdering() {
    }

    public static void apply() {
        List<CreativeModeTab> tabs = BuiltInRegistries.CREATIVE_MODE_TAB.stream().filter(tab -> !SHARED_TABS.contains(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab)))
                .sorted(Comparator.comparingInt((CreativeModeTab tab) -> ((FabricItemGroupImpl) tab).fabric_getPage()).thenComparing(CreativeModeTab::row).thenComparingInt(CreativeModeTab::column)).toList();
        boolean customOrder = tabs.stream().anyMatch(tab -> tab instanceof FabricCategorizedCreativeTab categorized && (!categorized.settings().tabsBefore().isEmpty() || !categorized.settings().tabsAfter().isEmpty()));
        if (!customOrder) {
            return;
        }
        Map<ResourceLocation, CreativeModeTab> byId = new LinkedHashMap<>();
        Map<CreativeModeTab, Set<CreativeModeTab>> edges = new LinkedHashMap<>();
        Map<CreativeModeTab, Integer> incoming = new HashMap<>();
        for (CreativeModeTab tab : tabs) {
            byId.put(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab), tab);
            edges.put(tab, new LinkedHashSet<>());
            incoming.put(tab, 0);
        }
        for (CreativeModeTab tab : tabs) {
            if (tab instanceof FabricCategorizedCreativeTab categorized) {
                for (ResourceLocation id : categorized.settings().tabsBefore()) {
                    addEdge(byId.get(id), tab, edges, incoming);
                }
                for (ResourceLocation id : categorized.settings().tabsAfter()) {
                    addEdge(tab, byId.get(id), edges, incoming);
                }
            }
        }
        List<CreativeModeTab> ordered = new ArrayList<>();
        Set<CreativeModeTab> remaining = new LinkedHashSet<>(tabs);
        while (!remaining.isEmpty()) {
            CreativeModeTab next = remaining.stream().filter(tab -> incoming.get(tab) == 0).findFirst().orElseThrow(() -> new IllegalStateException("Creative tab ordering contains a cycle"));
            remaining.remove(next);
            ordered.add(next);
            for (CreativeModeTab successor : edges.get(next)) {
                incoming.compute(successor, (tab, count) -> count - 1);
            }
        }
        for (int index = 0; index < ordered.size(); index++) {
            CreativeModeTab tab = ordered.get(index);
            int position = index % FabricItemGroupImpl.TABS_PER_PAGE;
            CreativeModeTab.Row row = position < FabricItemGroupImpl.TABS_PER_PAGE / 2 ? CreativeModeTab.Row.TOP : CreativeModeTab.Row.BOTTOM;
            ((FabricItemGroupImpl) tab).fabric_setPage(index / FabricItemGroupImpl.TABS_PER_PAGE);
            CreativeTabPositionMixin access = (CreativeTabPositionMixin) tab;
            access.lodestone$row(row);
            access.lodestone$column(position % (FabricItemGroupImpl.TABS_PER_PAGE / 2));
        }
    }

    private static void addEdge(CreativeModeTab before, CreativeModeTab after, Map<CreativeModeTab, Set<CreativeModeTab>> edges, Map<CreativeModeTab, Integer> incoming) {
        if (before != null && after != null && edges.get(before).add(after)) {
            incoming.compute(after, (tab, count) -> count + 1);
        }
    }
}
