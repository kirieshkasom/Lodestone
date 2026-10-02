package team.lodestar.lodestone.test;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.commands.Commands;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = TestContent.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeTestCommands {
    @SubscribeEvent
    public static void smoke(ClientTickEvent.Post event) {
        TestGpuSmoke.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void commands(RegisterClientCommandsEvent event) {
        TestClient.register(event.getDispatcher(), (source, component) -> source.sendSuccess(() -> component, false));
        event.getDispatcher().register(Commands.literal("lode").then(Commands.literal("tests")
                .then(Commands.literal("renderstate").executes(context -> {
                    NeoForgeSkyCheck.request(component -> context.getSource().sendSuccess(() -> component, false));
                    return 1;
                }))));
    }

    @SubscribeEvent
    public static void skyState(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            NeoForgeSkyCheck.capture(event.getCamera());
        }
    }
}
