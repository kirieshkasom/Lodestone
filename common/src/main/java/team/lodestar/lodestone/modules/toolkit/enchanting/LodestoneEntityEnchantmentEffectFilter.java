package team.lodestar.lodestone.modules.toolkit.enchanting;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.*;
import net.minecraft.core.*;
import net.minecraft.core.component.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import team.lodestar.lodestone.LodestoneLib;

import javax.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

public class LodestoneEntityEnchantmentEffectFilter<T extends EnchantmentEntityEffect> {


    public static <T extends EnchantmentEntityEffect> LodestoneEntityEnchantmentEffectFilter<T> byType(Class<T> effectClass) {
        return new LodestoneEntityEnchantmentEffectFilter<>(effectClass);
    }

    public static LodestoneEntityEnchantmentEffectFilter<EnchantmentEntityEffect> byRegistry(Holder<MapCodec<? extends EnchantmentEntityEffect>> effectType) {
        return byRegistry(effectType.value());
    }

    public static LodestoneEntityEnchantmentEffectFilter<EnchantmentEntityEffect> byRegistry(MapCodec<? extends EnchantmentEntityEffect> effectType) {
        return new LodestoneEntityEnchantmentEffectFilter<>(EnchantmentEntityEffect.class, effectType);
    }

    private final Class<T> classFilter;
    @Nullable
    private final MapCodec<? extends EnchantmentEntityEffect> typeFilter;
    @Nullable
    private Holder<Enchantment> enchantmentFilter;
    @Nullable
    private ItemStack comparisonBroker;
    @Nullable
    private Function<List<T>, T> deadlockBreaker;

    public LodestoneEntityEnchantmentEffectFilter(Class<T> classFilter) {
        this(classFilter, null);
    }

    public LodestoneEntityEnchantmentEffectFilter(Class<T> classFilter, @Nullable MapCodec<? extends EnchantmentEntityEffect> typeFilter) {
        this.classFilter = classFilter;
        this.typeFilter = typeFilter;
    }

    public LodestoneEntityEnchantmentEffectFilter<T> withEnchantmentFilter(Holder<Enchantment> enchantment) {
        this.enchantmentFilter = enchantment;
        return this;
    }

    public LodestoneEntityEnchantmentEffectFilter<T> withComparisonBroker(ItemStack comparisonBroker) {
        this.comparisonBroker = comparisonBroker;
        return this;
    }

    public LodestoneEntityEnchantmentEffectFilter<T> withDeadlockBreaker(Function<List<T>, T> deadlockBreaker) {
        this.deadlockBreaker = deadlockBreaker;
        return this;
    }

    protected Either<Predicate<TypedDataComponent<?>>, Predicate<T>> asCondition() {
        return Either.right(e -> (typeFilter == null || e.codec().equals(typeFilter)));
    }

    public Class<T> getClassFilter() {
        return classFilter;
    }

    @Nullable
    public Holder<Enchantment> getEnchantmentFilter() {
        return enchantmentFilter;
    }

    @Nullable
    public ItemStack getComparisonBroker(ItemStack fallback) {
        return comparisonBroker != null ? comparisonBroker : fallback;
    }

    public Optional<T> breakDeadlock(List<T> effects) {
        if (deadlockBreaker == null) {
            LodestoneLib.LOGGER.warn("An enchantment with two appropriate effects fit for it's query lacks a deadlock breaker. Report this to the mod dev responsible for the enchantment.");
            return Optional.empty();
        }
        return Optional.of(deadlockBreaker.apply(effects));
    }
}
