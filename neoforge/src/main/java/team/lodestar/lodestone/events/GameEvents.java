package team.lodestar.lodestone.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import team.lodestar.lodestone.handlers.*;
import team.lodestar.lodestone.neoforge.LodestoneNeoForgeItemResponses;
import team.lodestar.lodestone.modules.toolkit.enchanting.*;
import team.lodestar.lodestone.modules.toolkit.worldevent.*;

@EventBusSubscriber
public class GameEvents {

    @SubscribeEvent
    public static void modifyAttributes(ItemAttributeModifierEvent event) {
        LodestoneSlotBasedEnchantmentAttributeEffect.modifyAttributes(event.getItemStack(), modifier -> event.addModifier(modifier.attribute(), modifier.modifier(), modifier.slot()));
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LodestoneNeoForgeItemResponses.incoming(event);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        LodestoneNeoForgeItemResponses.before(event);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        LodestoneNeoForgeItemResponses.after(event);
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WorldEventHandler.playerJoin(player);
        }
    }

    @SubscribeEvent
    public static void worldTick(LevelTickEvent.Post event) {
        WorldEventHandler.worldTick(event.getLevel());
    }
}
