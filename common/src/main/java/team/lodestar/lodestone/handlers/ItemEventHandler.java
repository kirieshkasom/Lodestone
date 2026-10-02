package team.lodestar.lodestone.handlers;

import team.lodestar.lodestone.internal.LodestoneCommon;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Consumer;

/**
 * A handler for firing {@link IEventResponder} events
 */
public class ItemEventHandler {

    private static final HashSet<EventResponderSource> LOOKUPS = new HashSet<>();
    private static final ThreadLocal<IdentityHashMap<LivingEntity, DeathResponseScope>> ACTIVE_DEATH_SCOPES = ThreadLocal.withInitial(IdentityHashMap::new);

    public static final EventResponderSource HELD_ITEM = registerLookup(new EventResponderSource(LodestoneCommon.lodestonePath("held_item"), e -> List.of(e.getMainHandItem())));

    public static final EventResponderSource ARMOR = registerLookup(new EventResponderSource(LodestoneCommon.lodestonePath("armor"), e -> {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : e.getArmorSlots()) {
            stacks.add(stack);
        }
        return stacks;
    }));

    public static void triggerDeathResponses(DeathContext event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity target = event.target();
        LivingEntity attacker = attacker(event.source(), target);
        getEventResponders(target).forEach(lookup -> lookup.run((responder, stack) -> responder.incomingDeathEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((responder, stack) -> responder.outgoingDeathEvent(event, attacker, target, stack)));
        }
    }

    public static void enterDeathResponseScope(LivingEntity target) {
        IdentityHashMap<LivingEntity, DeathResponseScope> activeScopes = ACTIVE_DEATH_SCOPES.get();
        DeathResponseScope scope = activeScopes.get(target);
        if (scope == null) {
            scope = new DeathResponseScope();
            activeScopes.put(target, scope);
        }
        scope.depth++;
    }

    public static boolean claimDeathResponses(LivingEntity target) {
        DeathResponseScope scope = ACTIVE_DEATH_SCOPES.get().get(target);
        if (scope == null) {
            return true;
        }
        if (scope.dispatched) {
            return false;
        }
        scope.dispatched = true;
        return true;
    }

    public static void exitDeathResponseScope(LivingEntity target) {
        IdentityHashMap<LivingEntity, DeathResponseScope> activeScopes = ACTIVE_DEATH_SCOPES.get();
        DeathResponseScope scope = activeScopes.get(target);
        if (scope == null) {
            return;
        }
        scope.depth--;
        if (scope.depth == 0) {
            activeScopes.remove(target);
        }
        if (activeScopes.isEmpty()) {
            ACTIVE_DEATH_SCOPES.remove();
        }
    }

    public static DeathContext dispatchDeathResponses(LivingEntity target, DamageSource source) {
        if (!claimDeathResponses(target)) {
            return null;
        }
        DeathContext context = new DeathContext(target, source);
        triggerDeathResponses(context);
        return context;
    }

    private static final class DeathResponseScope {
        private int depth;
        private boolean dispatched;
    }

    public static void triggerHurtResponses(DamageContext event) {
        LivingEntity target = event.target();
        LivingEntity attacker = attacker(event.source(), target);
        getEventResponders(target).forEach(lookup -> lookup.run((responder, stack) -> {
            if (event.phase() == DamagePhase.AFTER_DAMAGE) {
                responder.finalizedIncomingDamageEvent(event, attacker, target, stack);
            } else {
                responder.incomingDamageEvent(event, attacker, target, stack);
            }
        }));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((responder, stack) -> {
                if (event.phase() == DamagePhase.AFTER_DAMAGE) {
                    responder.finalizedOutgoingDamageEvent(event, attacker, target, stack);
                } else {
                    responder.outgoingDamageEvent(event, attacker, target, stack);
                }
            }));
        }
    }

    private static LivingEntity attacker(DamageSource source, LivingEntity target) {
        return source.getEntity() instanceof LivingEntity attacker ? attacker : target.getLastAttacker();
    }

    public static void addAttributeTooltips(TooltipContext context) {
        if (context.stack().getItem() instanceof IEventResponder responder) {
            responder.modifyAttributeTooltipEvent(context);
        }
    }

    public enum DamagePhase {
        INCOMING,
        BEFORE_DAMAGE,
        AFTER_DAMAGE
    }

    public static class DamageContext {
        private final LivingEntity target;
        private final DamageSource source;
        private final DamagePhase phase;
        private float amount;
        private boolean canceled;

        public DamageContext(LivingEntity target, DamageSource source, DamagePhase phase, float amount) {
            this.target = target;
            this.source = source;
            this.phase = phase;
            this.amount = amount;
        }

        public LivingEntity target() {
            return target;
        }

        public DamageSource source() {
            return source;
        }

        public DamagePhase phase() {
            return phase;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            if (phase == DamagePhase.AFTER_DAMAGE) {
                throw new IllegalStateException("Final damage is read-only");
            }
            this.amount = amount;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            if (phase == DamagePhase.AFTER_DAMAGE) {
                throw new IllegalStateException("Final damage cannot be canceled");
            }
            this.canceled = canceled;
        }
    }

    public static class DeathContext {
        private final LivingEntity target;
        private final DamageSource source;
        private boolean canceled;

        public DeathContext(LivingEntity target, DamageSource source) {
            this.target = target;
            this.source = source;
        }

        public LivingEntity target() {
            return target;
        }

        public DamageSource source() {
            return source;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    public record TooltipContext(ItemStack stack, Consumer<Component> tooltip) {
        public void addTooltipLines(Component... lines) {
            for (Component line : lines) {
                tooltip.accept(line);
            }
        }

        public boolean shouldShow() {
            return stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).showInTooltip();
        }
    }

    public static List<EventResponderLookupResult> getEventResponders(LivingEntity entity) {
        return LOOKUPS.stream().map(s -> s.getEventResponders(entity)).toList();
    }

    public static EventResponderSource registerLookup(EventResponderSource lookup) {
        LOOKUPS.add(lookup);
        return lookup;
    }


    /**
     * An interface containing various methods which are triggered alongside damage, death, and tooltip callbacks.
     * Implement on your item for the methods to be called.
     * Does not necessarily have to be bound to an itemstack.
     */
    public interface IEventResponder {
        default void modifyAttributeTooltipEvent(TooltipContext context) {
        }

        default void incomingDamageEvent(DamageContext event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDamageEvent(DamageContext event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void finalizedIncomingDamageEvent(DamageContext event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void finalizedOutgoingDamageEvent(DamageContext event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void incomingDeathEvent(DeathContext event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDeathEvent(DeathContext event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }
    }

    public record EventResponderLookupResult(EventResponderSource source,
                                             ArrayList<Pair<IEventResponder, ItemStack>> result) {

        public void run(BiConsumer<IEventResponder, ItemStack> consumer) {
            run(IEventResponder.class, consumer);
        }

        public <T extends IEventResponder> void run(Class<T> type, BiConsumer<T, ItemStack> consumer) {
            for (Pair<IEventResponder, ItemStack> pair : result) {
                if (type.isInstance(pair.getFirst())) {
                    consumer.accept(type.cast(pair.getFirst()), pair.getSecond());
                }
            }
        }
    }

    public static class EventResponderSource {

        public final ResourceLocation id;
        public final Function<LivingEntity, Collection<ItemStack>> stackFunction;
        public final BiFunction<LivingEntity, ItemStack, IEventResponder> mapperFunction;

        public EventResponderSource(ResourceLocation id, Function<LivingEntity, Collection<ItemStack>> stackFunction) {
            this(id, stackFunction, (entity, stack) -> stack.getItem() instanceof IEventResponder eventResponderItem ? eventResponderItem : null);
        }

        public EventResponderSource(ResourceLocation id, Function<LivingEntity, Collection<ItemStack>> stackFunction, BiFunction<LivingEntity, ItemStack, IEventResponder> mapperFunction) {
            this.id = id;
            this.stackFunction = stackFunction;
            this.mapperFunction = mapperFunction;
        }

        public final EventResponderLookupResult getEventResponders(LivingEntity entity) {
            Collection<ItemStack> sourced = stackFunction.apply(entity);
            ArrayList<Pair<IEventResponder, ItemStack>> result = new ArrayList<>();
            for (ItemStack stack : sourced) {
                if (mapperFunction.apply(entity, stack) instanceof IEventResponder responderItem) {
                    result.add(Pair.of(responderItem, stack));
                }
            }
            return new EventResponderLookupResult(this, result);
        }
    }
}
