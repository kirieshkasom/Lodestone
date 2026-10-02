package team.lodestar.lodestone.handlers.screenparticle;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleOptions;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleHolder;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleItemStackKey;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleItemStackRetrievalKey;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.base.ScreenParticle;

import javax.annotation.*;
import java.util.*;

/**
 * A handler for screen particles.
 * Particles are spawned during rendering once per tick.
 * We also track all present ItemStacks on the screen to allow our particles to more optimally follow a given ItemStacks position
 * Use {@link ScreenParticleHandler#addParticle(ScreenParticleHolder, ScreenParticleOptions, double, double, double, double)} to create a screen particle, which will then be ticked.
 */
public class ScreenParticleHandler {

    /**
     * Item Stack Bound Particles are rendered just after an item stack in the inventory. They are ticked the same as other particles.
     * We use a pair of a boolean and the ItemStack as a key. The boolean sorts item particles based on if the ItemStack in question is in the hotbar or not.
     */
    public static final Map<ScreenParticleItemStackKey, ScreenParticleHolder> ITEM_PARTICLES = new HashMap<>();
    public static final Map<ScreenParticleItemStackRetrievalKey, ItemStack> ITEM_STACK_CACHE = new HashMap<>();
    public static final Collection<ScreenParticleItemStackRetrievalKey> ACTIVELY_ACCESSED_KEYS = new ArrayList<>();

    public static ScreenParticleHolder cachedItemParticles = null;
    public static int currentItemX, currentItemY;

    public static boolean canSpawnParticles;

    public static boolean renderingHotbar;

    public static void tickParticles() {
        ITEM_PARTICLES.values().forEach(ScreenParticleHolder::tick);
        ITEM_PARTICLES.values().removeIf(ScreenParticleHolder::isEmpty);

        ITEM_STACK_CACHE.keySet().removeIf(k -> !ACTIVELY_ACCESSED_KEYS.contains(k));
        ACTIVELY_ACCESSED_KEYS.clear();
        canSpawnParticles = true;
    }

    public static void renderTick(RenderFrameEvent.Post event) {
        canSpawnParticles = false;
    }

    public static void renderItemStackEarly(PoseStack poseStack, ItemStack stack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.player != null) {
            if (minecraft.isPaused()) {
                return;
            }
            if (!stack.isEmpty()) {
                var emitters = ParticleEmitterHandler.EMITTERS.get(stack.getItem());
                if (emitters != null) {
                    var pose = poseStack.last().pose();
                    currentItemX = x + 8;
                    currentItemY = y + 8;
                    if (currentItemX == 8 && currentItemY == 8) {
                        int poseOffsetX = (int) pose.m30();
                        int poseOffsetY = (int) pose.m31();
                        currentItemX += poseOffsetX;
                        currentItemY += poseOffsetY;
                    }
                    for (ParticleEmitterHandler.ItemParticleSupplier emitter : emitters) {
                        spawnAndPullParticles(minecraft.level, emitter, stack, false).render(poseStack);
                        cachedItemParticles = spawnAndPullParticles(minecraft.level, emitter, stack, true);
                    }
                }
            }
        }
    }

    public static void renderItemStackLate(PoseStack poseStack) {
        if (cachedItemParticles != null) {
            cachedItemParticles.render(poseStack);
            cachedItemParticles = null;
        }
    }

    public static ScreenParticleHolder spawnAndPullParticles(ClientLevel level, ParticleEmitterHandler.ItemParticleSupplier emitter, ItemStack stack, boolean isRenderedAfterItem) {
        ScreenParticleItemStackRetrievalKey cacheKey = new ScreenParticleItemStackRetrievalKey(renderingHotbar, isRenderedAfterItem, currentItemX, currentItemY);
        ScreenParticleHolder target = ITEM_PARTICLES.computeIfAbsent(new ScreenParticleItemStackKey(renderingHotbar, isRenderedAfterItem, stack), s -> new ScreenParticleHolder());
        pullFromParticleVault(cacheKey, stack, target, isRenderedAfterItem);
        if (canSpawnParticles) {
            if (isRenderedAfterItem) {
                emitter.spawnLateParticles(target, level, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false), stack, currentItemX, currentItemY);
            } else {
                emitter.spawnEarlyParticles(target, level, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false), stack, currentItemX, currentItemY);
            }
        }
        ACTIVELY_ACCESSED_KEYS.add(cacheKey);
        return target;
    }

    public static void pullFromParticleVault(ScreenParticleItemStackRetrievalKey cacheKey, ItemStack currentStack, ScreenParticleHolder target, boolean isRenderedAfterItem) {
        if (ITEM_STACK_CACHE.containsKey(cacheKey)) {
            ItemStack oldStack = ITEM_STACK_CACHE.get(cacheKey);
            if (oldStack != currentStack && oldStack.getItem().equals(currentStack.getItem())) {
                ScreenParticleItemStackKey oldKey = new ScreenParticleItemStackKey(renderingHotbar, isRenderedAfterItem, oldStack);
                ScreenParticleHolder oldParticles = ITEM_PARTICLES.get(oldKey);
                if (oldParticles != null) {
                    target.addFrom(oldParticles);
                }
                ITEM_STACK_CACHE.remove(cacheKey);
                ITEM_PARTICLES.remove(oldKey);
            }
        }
        ITEM_STACK_CACHE.put(cacheKey, currentStack);
    }

    @Deprecated
    protected static void renderParticles(ScreenParticleHolder screenParticleTarget) {
        screenParticleTarget.render();
    }

    @Deprecated
    protected static void renderParticles(ScreenParticleHolder screenParticleTarget, @Nullable PoseStack poseStack) {
        screenParticleTarget.render(poseStack);
    }

    public static void clearParticles() {
        ITEM_PARTICLES.values().forEach(ScreenParticleHandler::clearParticles);
    }

    public static void clearParticles(ScreenParticleHolder screenParticleTarget) {
        screenParticleTarget.particles.values().forEach(ArrayList::clear);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ScreenParticleOptions> ScreenParticle addParticle(ScreenParticleHolder screenParticleTarget, T options, double x, double y, double xMotion, double yMotion) {
        Minecraft minecraft = Minecraft.getInstance();
        ScreenParticleType<T> type = (ScreenParticleType<T>) options.type;
        ScreenParticle particle = type.provider.createParticle(minecraft.level, options, x, y, xMotion, yMotion);
        ArrayList<ScreenParticle> list = screenParticleTarget.particles.computeIfAbsent(options.renderType, (a) -> new ArrayList<>());
        list.add(particle);
        return particle;
    }
}