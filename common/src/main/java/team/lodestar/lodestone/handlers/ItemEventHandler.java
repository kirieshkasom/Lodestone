package team.lodestar.lodestone.handlers;

import com.mojang.datafixers.util.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.entity.living.*;
import team.lodestar.lodestone.*;

import java.util.*;
import java.util.function.*;

/**
 * A handler for firing {@link IEventResponder} events
 */
public class ItemEventHandler {

    private static final HashSet<EventResponderSource> LOOKUPS = new HashSet<>();

    public static final EventResponderSource HELD_ITEM = registerLookup(new EventResponderSource(LodestoneLib.lodestonePath("held_item"), e -> List.of(e.getMainHandItem())));

    public static final EventResponderSource ARMOR = registerLookup(new EventResponderSource(LodestoneLib.lodestonePath("armor"), e -> {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : e.getArmorSlots()) {
            stacks.add(stack);
        }
        return stacks;
    }));

    public static void triggerDeathResponses(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        var source = event.getSource();
        var target = event.getEntity();
        var attacker = source.getEntity() instanceof LivingEntity livingAttacker ? livingAttacker : target.getLastAttacker();
        getEventResponders(target).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.incomingDeathEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.outgoingDeathEvent(event, attacker, target, stack)));
        }
    }

    public static void triggerHurtResponses(LivingIncomingDamageEvent event) {
        var source = event.getSource();
        var target = event.getEntity();
        var attacker = source.getEntity() instanceof LivingEntity livingAttacker ? livingAttacker : target.getLastAttacker();
        getEventResponders(target).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.incomingDamageEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.outgoingDamageEvent(event, attacker, target, stack)));
        }
    }

    public static void triggerHurtResponses(LivingDamageEvent.Pre event) {
        var source = event.getSource();
        var target = event.getEntity();
        var attacker = source.getEntity() instanceof LivingEntity livingAttacker ? livingAttacker : target.getLastAttacker();
        getEventResponders(target).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.incomingDamageEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.outgoingDamageEvent(event, attacker, target, stack)));
        }
    }

    public static void triggerHurtResponses(LivingDamageEvent.Post event) {
        var source = event.getSource();
        var target = event.getEntity();
        var attacker = source.getEntity() instanceof LivingEntity livingAttacker ? livingAttacker : target.getLastAttacker();
        getEventResponders(target).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.finalizedIncomingDamageEvent(event, attacker, target, stack)));
        if (attacker != null) {
            getEventResponders(attacker).forEach(lookup -> lookup.run((eventResponderItem, stack) -> eventResponderItem.finalizedOutgoingDamageEvent(event, attacker, target, stack)));
        }
    }

    public static void addAttributeTooltips(AddAttributeTooltipsEvent event) {
        final ItemStack stack = event.getStack();
        if (stack.getItem() instanceof IEventResponder eventResponderItem) {
            eventResponderItem.modifyAttributeTooltipEvent(event);
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
     * An interface containing various methods which are triggered alongside various forge events.
     * Implement on your item for the methods to be called.
     * Does not necessarily have to be bound to an itemstack.
     */
    public interface IEventResponder {

        default void modifyAttributeTooltipEvent(AddAttributeTooltipsEvent event) {

        }

        //TODO: the naming here is confusing. We have the `Incoming` event which is ran before any damage is done
        // We also have Our Incoming and Outgoing wrappers for LivingDamageEvent Pre and Post
        // Need to figure out a nice naming consistency here
        default void incomingDamageEvent(LivingIncomingDamageEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDamageEvent(LivingIncomingDamageEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void incomingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDamageEvent(LivingDamageEvent.Pre event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void finalizedIncomingDamageEvent(LivingDamageEvent.Post event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void finalizedOutgoingDamageEvent(LivingDamageEvent.Post event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void incomingDeathEvent(LivingDeathEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        }

        default void outgoingDeathEvent(LivingDeathEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
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