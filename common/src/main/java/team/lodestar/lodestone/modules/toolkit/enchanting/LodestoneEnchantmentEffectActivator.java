package team.lodestar.lodestone.modules.toolkit.enchanting;

import com.mojang.datafixers.util.*;
import net.minecraft.core.Holder;
import net.minecraft.core.component.*;
import net.minecraft.server.level.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.phys.*;
import org.apache.commons.lang3.mutable.*;

import javax.annotation.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

/**
 * A helper class to activate enchantment effects in a more convenient way than through the use of {@link EnchantmentHelper}.
 */
public class LodestoneEnchantmentEffectActivator<T> {

    /**
     * Creates an effect activator for non-targeted conditional effects.
     *
     * @param componentType The data component type to activate.
     * @param level         A {@link ServerLevel} instance.
     * @return The created effect activator.
     */
    public static <T> LodestoneEnchantmentEffectActivator<T> createEffectActivator(DataComponentType<List<ConditionalEffect<T>>> componentType, ServerLevel level) {
        return new LodestoneEnchantmentEffectActivator<>(Either.left(componentType), level);
    }

    /**
     * Creates an effect activator for targeted conditional effects.
     *
     * @param componentType The data component type to activate.
     * @param level         A {@link ServerLevel} instance.
     * @return The created effect activator.
     */
    public static <T> LodestoneEnchantmentEffectActivator<T> createTargetedEffectActivator(DataComponentType<List<TargetedConditionalEffect<T>>> componentType, ServerLevel level) {
        return new LodestoneEnchantmentEffectActivator<>(Either.right(componentType), level);
    }


    private final Either<DataComponentType<List<ConditionalEffect<T>>>, DataComponentType<List<TargetedConditionalEffect<T>>>> componentType;
    private final ServerLevel level;

    private ContextSupplier contextSupplier;
    private EnchantmentTarget targetLimit;

    private LodestoneEnchantmentEffectIntercom.AttachedIntercom<?> intercom;

    private LodestoneEnchantmentEffectActivator(Either<DataComponentType<List<ConditionalEffect<T>>>, DataComponentType<List<TargetedConditionalEffect<T>>>> componentType, ServerLevel level) {
        this.componentType = componentType;
        this.level = level;
    }

    public LodestoneEnchantmentEffectActivator<T> setItemContext() {
        return setContext(((enchantedItem, enchantmentLevel) -> Enchantment.itemContext(level, enchantmentLevel, enchantedItem)));
    }

    public LodestoneEnchantmentEffectActivator<T> setDamageContext(ServerLevel level, Entity attackedEntity, DamageSource damageSource) {
        return setContext(((enchantedItem, enchantmentLevel) -> damageContext(level, enchantmentLevel, attackedEntity, damageSource, enchantedItem)));
    }

    public LodestoneEnchantmentEffectActivator<T> setEntityContext(ServerLevel level, Entity entity) {
        return setEntityContext(level, entity, entity.position());
    }

    public LodestoneEnchantmentEffectActivator<T> setEntityContext(ServerLevel level, Entity entity, Vec3 origin) {
        return setContext(((enchantedItem, enchantmentLevel) -> entityContext(level, enchantmentLevel, entity, origin, enchantedItem)));
    }

    public LodestoneEnchantmentEffectActivator<T> setContext(ContextSupplier contextSupplier) {
        this.contextSupplier = contextSupplier;
        return this;
    }

    public LodestoneEnchantmentEffectActivator<T> setTargetLimit(EnchantmentTarget targetLimit) {
        this.targetLimit = targetLimit;
        return this;
    }

    public <K extends LodestoneEnchantmentEffectIntercom.EnchantmentIntercomData> LodestoneEnchantmentEffectActivator<T> attachIntercom(LodestoneEnchantmentEffectIntercom<K> intercom, Supplier<K> data) {
        this.intercom = intercom.attach(data);
        return this;
    }

    public void triggerEntityEffects(ItemStack enchantedItem, LivingEntity target) {
        triggerEntityEffects(enchantedItem, target, target);
    }

    public void triggerEntityEffects(ItemStack enchantedItem, LivingEntity enchanted, Entity target) {
        triggerEntityEffects(new EnchantedItemInUse(enchantedItem, null, enchanted, (item) -> {
        }), target);
    }

    public void triggerEntityEffects(EnchantedItemInUse enchantedItem, Entity target) {
        applyEffectsOnItem((effect, entity, enchantmentLevel) -> {
            if (effect instanceof EnchantmentEntityEffect entityEffect) {
                entityEffect.apply(level, enchantmentLevel, enchantedItem, entity, entity.position());
            }
        }, enchantedItem.itemStack(), target);
    }

    /**
     * Modifies the value provided using all {@link EnchantmentValueEffect} instances on the given item for the given target.
     *
     * @param enchantedItem The enchanted item.
     * @param target        The entity the item belongs to.
     * @return The total value of the enchantment value effect provided by the given item.
     */
    public float countValue(ItemStack enchantedItem, Entity target) {
        return modifyValue(enchantedItem, target, 0f);
    }

    /**
     * Modifies the value provided using all {@link EnchantmentValueEffect} instances on the given item for the given target.
     *
     * @param enchantedItem The enchanted item.
     * @param target        The entity the item belongs to.
     * @param baseValue     The base value to modify.
     * @return The total value of the enchantment value effect provided by the given item.
     */
    public float modifyValue(ItemStack enchantedItem, Entity target, float baseValue) {
        MutableFloat value = new MutableFloat(baseValue);
        applyEffectsOnItem((effect, entity, enchantmentLevel) -> {
            if (effect instanceof EnchantmentValueEffect valueEffect) {
                value.setValue(valueEffect.process(enchantmentLevel, level.getRandom(), value.getValue()));
            }
        }, enchantedItem, target);
        return value.getValue();
    }

    /**
     * Modifies the value provided using all {@link EnchantmentValueEffect} instances on the given item for the given target.
     *
     * @param target The entity the item belongs to.
     * @return The total value of the enchantment value effect based on all items influencing the target entity.
     */
    public float countValue(LivingEntity target) {
        return modifyValue(target, 0f);
    }

    /**
     * Modifies the value provided using all {@link EnchantmentValueEffect} instances on the given item for the given target.
     *
     * @param target    The entity the item belongs to.
     * @param baseValue The base value to modify.
     * @return The total value of the enchantment value effect based on all items influencing the target entity.
     */
    public float modifyValue(LivingEntity target, float baseValue) {
        MutableFloat value = new MutableFloat(baseValue);
        applyEffectsOnEquipment((effect, entity, enchantmentLevel) -> {
            if (effect instanceof EnchantmentValueEffect valueEffect) {
                value.setValue(valueEffect.process(enchantmentLevel, level.getRandom(), value.getValue()));
            }
        }, target);
        return value.getValue();
    }

    /**
     * Checks whether the given item has any effect instance of the type handled by this activator.
     * @param enchantedItem The enchanted item.
     * @param target The entity the item belongs to.
     * @return True if at least one effect is present, false otherwise.
     */
    public boolean hasEffect(ItemStack enchantedItem, Entity target) {
        AtomicBoolean hasEffect = new AtomicBoolean(false);
        applyEffectsOnItem((effect, entity, enchantmentLevel) -> hasEffect.set(true), enchantedItem, target);
        return hasEffect.get();
    }
    /**
     * Triggers enchantment effects present on a specified item.
     *
     * @param acceptor      The effect acceptor
     * @param enchantedItem The enchanted item.
     * @param target        The entity to affect. In case of targeted effects, this ends up being the victim.
     */
    public void applyEffectsOnItem(EnchantmentEffectAcceptor<T> acceptor, ItemStack enchantedItem, Entity target) {
        if (contextSupplier == null) {
            throw new IllegalStateException("Context is not set");
        }

        EnchantmentHelper.runIterationOnItem(enchantedItem, ((enchantment, enchantmentLevel) -> {
            var context = contextSupplier.getContext(enchantedItem, enchantmentLevel);
            applyEffects(acceptor, target, enchantment, enchantmentLevel, context);
        }));
    }

    /**
     * Triggers all enchantment effects available to the given entity.
     *
     * @param acceptor The effect acceptor.
     * @param target   The entity to affect. In case of targeted effects, this ends up being the victim.
     */
    public void applyEffectsOnEquipment(EnchantmentEffectAcceptor<T> acceptor, LivingEntity target) {
        if (contextSupplier == null) {
            throw new IllegalStateException("Context is not set");
        }

        EnchantmentHelper.runIterationOnEquipment(target, ((enchantment, enchantmentLevel, enchantedItem) -> {
            var context = contextSupplier.getContext(enchantedItem.itemStack(), enchantmentLevel);
            applyEffects(acceptor, target, enchantment, enchantmentLevel, context);
        }));
    }

    private void applyEffects(EnchantmentEffectAcceptor<T> acceptor, Entity target, Holder<Enchantment> enchantment, int enchantmentLevel, LootContext context) {
        componentType.ifLeft(componentType -> {
            for (ConditionalEffect<T> effect : enchantment.value().getEffects(componentType)) {
                if (effect.matches(context)) {
                    if (intercom != null) {
                        intercom.write();
                    }
                    acceptor.apply(effect.effect(), target, enchantmentLevel);
                }
            }
        });
        componentType.ifRight(componentType -> {
            for (TargetedConditionalEffect<T> effect : enchantment.value().getEffects(componentType)) {
                if (targetLimit != null && effect.enchanted() != targetLimit) {
                    continue;
                }
                if (effect.matches(context)) {
                    DamageSource source = context.getParam(LootContextParams.DAMAGE_SOURCE);
                    Entity entity = switch (effect.affected()) {
                        case ATTACKER -> source.getEntity();
                        case DAMAGING_ENTITY -> source.getDirectEntity();
                        case VICTIM -> target;
                    };
                    if (entity != null) {
                        if (intercom != null) {
                            intercom.write();
                        }
                        acceptor.apply(effect.effect(), entity, enchantmentLevel);
                    }
                }
            }
        });
        if (intercom != null) {
            intercom.clear();
        }
    }

    public static LootContext entityContext(ServerLevel level, int enchantmentLevel, Entity entity, Vec3 origin, @Nullable ItemStack tool) {
        LootParams lootparams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ENCHANTMENT_LEVEL, enchantmentLevel)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withOptionalParameter(LootContextParams.TOOL, tool)
                .create(LootContextParamSets.ENCHANTED_ENTITY);
        return new LootContext.Builder(lootparams).create(Optional.empty());
    }

    public static LootContext damageContext(ServerLevel level, int enchantmentLevel, Entity entity, DamageSource damageSource, @Nullable ItemStack tool) {
        LootParams lootparams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ENCHANTMENT_LEVEL, enchantmentLevel)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .withOptionalParameter(LootContextParams.TOOL, tool)
                .create(LootContextParamSets.ENCHANTED_DAMAGE);
        return new LootContext.Builder(lootparams).create(Optional.empty());
    }

    public interface ContextSupplier {
        LootContext getContext(ItemStack item, int enchantmentLevel);
    }

    public interface EnchantmentEffectAcceptor<T> {
        default void apply(T effect, int enchantmentLevel) {
            apply(effect, null, enchantmentLevel);
        }

        void apply(T effect, Entity target, int enchantmentLevel);
    }
}