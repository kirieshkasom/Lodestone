package team.lodestar.lodestone.test;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import team.lodestar.lodestone.registry.client.LodestonePostProcessEffects;

public final class NeoForgeSkyCheck {
    private static Consumer<Component> pending;

    private NeoForgeSkyCheck() {
    }

    public static void request(Consumer<Component> feedback) {
        pending = feedback;
    }

    public static void capture(Camera camera) {
        if (pending == null) {
            return;
        }
        Consumer<Component> feedback = pending;
        pending = null;
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget target = minecraft.getMainRenderTarget();
        int oldReadFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int drawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer clear = stack.mallocFloat(4);
            GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, clear);
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, target.frameBufferId);
            ByteBuffer lower = stack.malloc(4);
            ByteBuffer upper = stack.malloc(4);
            GL11.glReadPixels(target.width / 2, target.height / 4, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lower);
            GL11.glReadPixels(target.width / 2, target.height * 3 / 4, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, upper);
            double horizon = minecraft.level.getLevelData().getHorizonHeight(minecraft.level);
            String report = String.format(Locale.ROOT,
                    "sky: cameraY=%.2f horizon=%.2f clearRGB=%.3f,%.3f,%.3f lowerRGB=%s upperRGB=%s drawTarget=%d mainTarget=%d bloom=%s",
                    camera.getPosition().y, horizon, clear.get(0), clear.get(1), clear.get(2),
                    rgb(lower), rgb(upper), drawFramebuffer, target.frameBufferId, LodestonePostProcessEffects.BLOOM.isActive());
            feedback.accept(Component.literal(report));
            team.lodestar.lodestone.internal.LodestoneCommon.LOGGER.info(report);
        } finally {
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, oldReadFramebuffer);
        }
    }

    private static String rgb(ByteBuffer pixel) {
        return String.format(Locale.ROOT, "%02x%02x%02x", Byte.toUnsignedInt(pixel.get(0)), Byte.toUnsignedInt(pixel.get(1)), Byte.toUnsignedInt(pixel.get(2)));
    }
}
