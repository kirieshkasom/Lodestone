package team.lodestar.lodestone.test;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.internal.network.LodestoneNetworking;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;
import team.lodestar.lodestone.modules.toolkit.screenshake.ScreenshakePayload;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventHandler;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;
import team.lodestar.lodestone.modules.toolkit.worldevent.UpdateWorldEventPayload;

public final class TestServerCommands {
    private TestServerCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lode").then(Commands.literal("tests").requires(source -> source.hasPermission(2))
                .then(Commands.literal("givearmor").executes(context -> {
                    ItemStack stack = new ItemStack(TestContent.HELMET.get());
                    stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                    context.getSource().sendSuccess(() -> Component.literal("Test crown added. Equip it and use F5; inspect the glint and hotbar/inventory sparkles."), false);
                    return 1;
                }))
                .then(Commands.literal("networkshake").executes(context -> {
                    LodestoneNetworking.sendToPlayer(context.getSource().getPlayerOrException(), new ScreenshakePayload(builder -> builder.setDuration(40).setStrength(0.6f, 0)));
                    context.getSource().sendSuccess(() -> Component.literal("Sent a two-second screenshake through the server payload."), false);
                    return 1;
                }))
                .then(Commands.literal("worldevent")
                        .then(Commands.literal("freeze").executes(context -> setFrozen(context.getSource(), true)))
                        .then(Commands.literal("unfreeze").executes(context -> setFrozen(context.getSource(), false)))
                        .then(Commands.literal("create").executes(context -> {
                            TestWorldEvent event = new TestWorldEvent(TestContent.EVENT.get());
                            event.position = context.getSource().getPosition().add(0, 2, 0);
                            WorldEventHandler.addWorldEvent(context.getSource().getLevel(), event);
                            context.getSource().sendSuccess(() -> Component.literal("Created a 60-second beacon event " + event.uuid + ". Reconnect or restart to check persistence."), false);
                            return 1;
                        }))
                        .then(Commands.literal("status").executes(context -> {
                            int count = 0;
                            for (WorldEventInstance event : WorldEventStorageAccess.get(context.getSource().getLevel()).activeWorldEvents) {
                                if (event instanceof TestWorldEvent test) {
                                    context.getSource().sendSuccess(() -> Component.literal("Server beacon " + test.uuid + " age=" + test.age + "/" + test.duration + " frozen=" + test.frozen), false);
                                    count++;
                                }
                            }
                            return count;
                        }))
                        .then(Commands.literal("clear").executes(context -> {
                            int count = 0;
                            for (WorldEventInstance event : WorldEventStorageAccess.get(context.getSource().getLevel()).activeWorldEvents) {
                                if (event instanceof TestWorldEvent) {
                                    event.end(context.getSource().getLevel());
                                    LodestoneNetworking.sendToAllPlayers(new UpdateWorldEventPayload(event));
                                    count++;
                                }
                            }
                            return count;
                        })))));
        dispatcher.register(Commands.literal("lodestone_tests").requires(source -> source.hasPermission(2))
                .redirect(dispatcher.getRoot().getChild("lode").getChild("tests")));
    }

    private static int setFrozen(CommandSourceStack source, boolean frozen) {
        int count = 0;
        for (WorldEventInstance event : WorldEventStorageAccess.get(source.getLevel()).activeWorldEvents) {
            if (event instanceof TestWorldEvent) {
                event.frozen = frozen;
                event.setDirty();
                count++;
            }
        }
        int affected = count;
        source.sendSuccess(() -> Component.literal((frozen ? "Froze " : "Unfroze ") + affected + " test events."), false);
        return count;
    }
}
