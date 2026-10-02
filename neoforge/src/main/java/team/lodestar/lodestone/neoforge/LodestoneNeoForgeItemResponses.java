package team.lodestar.lodestone.neoforge;

import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import team.lodestar.lodestone.handlers.ItemEventHandler;
import team.lodestar.lodestone.handlers.ItemEventHandler.DamageContext;
import team.lodestar.lodestone.handlers.ItemEventHandler.DamagePhase;
import team.lodestar.lodestone.handlers.ItemEventHandler.TooltipContext;

public final class LodestoneNeoForgeItemResponses {
    private LodestoneNeoForgeItemResponses() {
    }

    public static void incoming(LivingIncomingDamageEvent event) {
        if (event.isCanceled()) {
            return;
        }
        DamageContext context = new DamageContext(event.getEntity(), event.getSource(), DamagePhase.INCOMING, event.getAmount());
        ItemEventHandler.triggerHurtResponses(context);
        event.setAmount(context.getAmount());
        event.setCanceled(context.isCanceled());
    }

    public static void before(LivingDamageEvent.Pre event) {
        DamageContext context = new DamageContext(event.getEntity(), event.getSource(), DamagePhase.BEFORE_DAMAGE, event.getNewDamage());
        ItemEventHandler.triggerHurtResponses(context);
        event.setNewDamage(context.isCanceled() ? 0 : context.getAmount());
    }

    public static void after(LivingDamageEvent.Post event) {
        ItemEventHandler.triggerHurtResponses(new DamageContext(event.getEntity(), event.getSource(), DamagePhase.AFTER_DAMAGE, event.getNewDamage()));
    }

    public static void tooltip(AddAttributeTooltipsEvent event) {
        ItemEventHandler.addAttributeTooltips(new TooltipContext(event.getStack(), line -> event.addTooltipLines(line)));
    }
}
