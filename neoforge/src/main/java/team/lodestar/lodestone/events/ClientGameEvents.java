package team.lodestar.lodestone.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import team.lodestar.lodestone.handlers.*;
import team.lodestar.lodestone.handlers.screenparticle.ScreenParticleHandler;
import team.lodestar.lodestone.modules.rendering.*;
import team.lodestar.lodestone.modules.toolkit.worldevent.*;
import team.lodestar.lodestone.internal.client.LodestoneClientLifecycle;


@EventBusSubscriber(value = Dist.CLIENT)
public class ClientGameEvents {

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        team.lodestar.lodestone.registry.client.LodestoneClientCommands.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LodestoneClientLifecycle.clientTick(minecraft, WorldEventHandler::tick);
    }

    @SubscribeEvent
    public static void cameraSetup(ViewportEvent.ComputeCameraAngles event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        ScreenshakeHandler.applyCameraJitter(level.getRandom(), new ScreenshakeHandler.CameraAngles() {
            @Override
            public float getYaw() {
                return event.getYaw();
            }

            @Override
            public float getPitch() {
                return event.getPitch();
            }

            @Override
            public void setYaw(float yaw) {
                event.setYaw(yaw);
            }

            @Override
            public void setPitch(float pitch) {
                event.setPitch(pitch);
            }
        });
    }

    @SubscribeEvent
    public static void addAttributeTooltips(AddAttributeTooltipsEvent event) {
        team.lodestar.lodestone.neoforge.LodestoneNeoForgeItemResponses.tooltip(event);
    }

    @SubscribeEvent
    public static void renderFrameEvent(RenderFrameEvent.Post event) {
        ScreenParticleHandler.renderTick();
    }

    @SubscribeEvent
    public static void shutdownEvent(GameShuttingDownEvent event) {
        LodestoneClientLifecycle.shutdown();
    }
}
