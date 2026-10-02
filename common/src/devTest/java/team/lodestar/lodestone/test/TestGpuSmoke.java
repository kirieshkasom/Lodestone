package team.lodestar.lodestone.test;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.rendering.RenderPhase;
import team.lodestar.lodestone.modules.rendering.handlers.ParticleHandler;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.postprocess.PostProcessor;
import team.lodestar.lodestone.modules.rendering.postprocess.effects.BloomPostProcessor;
import team.lodestar.lodestone.registry.client.LodestonePostProcessEffects;

public final class TestGpuSmoke {
    private static boolean started;

    private TestGpuSmoke() {
    }

    public static void tick(Minecraft minecraft) {
        String directory = System.getProperty("lodestone.tests.smoke");
        if (directory == null || started || minecraft.getOverlay() != null || !(minecraft.screen instanceof TitleScreen)) {
            return;
        }
        started = true;
        RenderSystem.recordRenderCall(() -> run(minecraft, Path.of(directory)));
    }

    private static void run(Minecraft minecraft, Path directory) {
        String result;
        try {
            Files.createDirectories(directory);
            Files.writeString(directory.resolve("result.txt"), "RUNNING\n");
            LodestoneCommon.LOGGER.info("Development GPU smoke: starting pooled draw");
            pooled(minecraft);
            LodestoneCommon.LOGGER.info("Development GPU smoke: starting bloom chain");
            bloom(minecraft);
            result = "PASS: real pooled renderer preserves transparent corners and draws the billboard; bloom loads and preserves zero-alpha sky RGB and the main framebuffer.\n";
        } catch (Throwable error) {
            StringWriter trace = new StringWriter();
            error.printStackTrace(new PrintWriter(trace));
            result = "FAIL\n" + trace;
            LodestoneCommon.LOGGER.error("Development GPU smoke failed", error);
        }
        try {
            Files.writeString(directory.resolve("result.txt"), result);
            LodestoneCommon.LOGGER.info(result);
        } catch (Exception error) {
            LodestoneCommon.LOGGER.error("Could not write development GPU smoke result", error);
        } finally {
            minecraft.stop();
        }
    }

    private static void pooled(Minecraft minecraft) {
        RenderTarget target = new TextureTarget(32, 32, true, Minecraft.ON_OSX);
        try {
            target.setClearColor(0.125f, 0.25f, 0.375f, 0);
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            ParticleHandler.spawn(TestClient.pooledSpec(), new ParticleSpawnContext()
                    .position(0, 0, -2).scale(2, 2, 2).color(0.2f, 0.9f, 1, 1).lifetime(100));
            ParticleHandler.render(RenderPhase.AFTER_PARTICLES, 1, new Matrix4f(), new Matrix4f().setOrtho(-1, 1, -1, 1, 0.1f, 10));
            int[] corner = pixel(target, 1, 1);
            requireClose(corner, 32, 64, 96, "Pooled corner overwrote the background");
            int[] center = pixel(target, 16, 16);
            if (center[1] <= 64 || center[2] <= 96) {
                throw new AssertionError("Pooled billboard center did not render");
            }
            if (GL11.glGetError() != GL11.GL_NO_ERROR) {
                throw new AssertionError("OpenGL error during pooled draw");
            }
        } finally {
            ParticleHandler.clearClientParticles();
            target.destroyBuffers();
            minecraft.getMainRenderTarget().bindWrite(true);
        }
    }

    private static void bloom(Minecraft minecraft) {
        BloomPostProcessor bloom = LodestonePostProcessEffects.BLOOM;
        if (bloom.getBloomTarget() == null) {
            throw new AssertionError("Bloom chain failed to load");
        }
        RenderTarget main = minecraft.getMainRenderTarget();
        main.bindWrite(true);
        RenderSystem.clearColor(0.7f, 0.8f, 1, 0);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        bloom.getBloomTarget().clear(Minecraft.ON_OSX);
        main.bindWrite(true);
        PostProcessor.viewModelMatrix = new Matrix4f();
        bloom.setActive(true);
        try {
            bloom.applyPostProcess();
            int drawTarget = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            if (drawTarget != main.frameBufferId) {
                throw new AssertionError("Bloom cleanup left another framebuffer bound");
            }
            requireClose(pixel(main, main.width / 2, main.height / 4), 179, 204, 255, "Bloom erased zero-alpha sky RGB");
        } finally {
            bloom.setActive(false);
            main.bindWrite(true);
        }
    }

    private static int[] pixel(RenderTarget target, int x, int y) {
        int previous = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, target.frameBufferId);
            ByteBuffer pixel = stack.malloc(4);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);
            return new int[]{Byte.toUnsignedInt(pixel.get(0)), Byte.toUnsignedInt(pixel.get(1)), Byte.toUnsignedInt(pixel.get(2))};
        } finally {
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, previous);
        }
    }

    private static void requireClose(int[] actual, int red, int green, int blue, String message) {
        if (Math.abs(actual[0] - red) > 2 || Math.abs(actual[1] - green) > 2 || Math.abs(actual[2] - blue) > 2) {
            throw new AssertionError(message + ": " + actual[0] + "," + actual[1] + "," + actual[2]);
        }
    }
}
