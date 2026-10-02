package team.lodestar.lodestone.test;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler.ItemParticleSupplier;
import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;
import team.lodestar.lodestone.modules.rendering.handlers.ParticleHandler;
import team.lodestar.lodestone.modules.rendering.model.entity.EntityModelHolder;
import team.lodestar.lodestone.modules.rendering.model.entity.armor.LodestoneArmorModel;
import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleBuilder;
import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleSpec;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.standard.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.modules.rendering.particle.standard.builder.WorldParticleBuilder;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.GenericParticleData;
import team.lodestar.lodestone.modules.rendering.particle.standard.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.ScreenParticleHolder;
import team.lodestar.lodestone.modules.rendering.postprocess.effects.BloomPostProcessor;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;
import team.lodestar.lodestone.registry.client.LodestoneParticleVisuals;
import team.lodestar.lodestone.registry.client.LodestonePostProcessEffects;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.registry.client.LodestoneShaders;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleTypes;
import team.lodestar.lodestone.systems.rendering.rendeertype.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

public final class TestClient {
    public static int modelBakes;
    public static final EntityModelHolder<LodestoneArmorModel> ARMOR = new EntityModelHolder<>(TestContent.id("crown"), root -> {
        modelBakes++;
        return new LodestoneArmorModel(root);
    }, () -> LodestoneArmorModel.createArmorModel((mesh, root, head, body, waist, rightArm, leftArm, rightLeg, leftLeg, rightFoot, leftFoot) -> {
        head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(0, 0).addBox(-6, -11, -6, 12, 3, 12), PartPose.ZERO);
        head.addOrReplaceChild("spire", CubeListBuilder.create().texOffs(0, 0).addBox(-2, -16, -2, 4, 5, 4), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }));
    public static final ShaderHolder POOLED_SHADER = new ShaderHolder(TestContent.id("pooled"), DefaultVertexFormat.POSITION_TEX);
    private static RenderType pooledType;

    private TestClient() {
    }

    public static void install() {
        ParticleEmitterHandler.registerItemParticleEmitter(TestContent.HELMET.get(),
                new ItemParticleSupplier() {
                    @Override
                    public void spawnLateParticles(ScreenParticleHolder target,
                            Level level, float partialTicks, ItemStack stack, float x, float y) {
                        ScreenParticleBuilder.create(
                                LodestoneScreenParticleTypes.SPARKLE, target)
                                .setLifetime(12).setScaleData(GenericParticleData.create(0.25f, 0).build())
                                .setMotion(0, -0.4).spawnOnStack(0, 0);
                    }
                });
    }

    public static <S> void register(CommandDispatcher<S> dispatcher, BiConsumer<S, Component> feedback) {
        LiteralArgumentBuilder<S> tests = LiteralArgumentBuilder.<S>literal("tests")
                .then(LiteralArgumentBuilder.<S>literal("screenshake").executes(context -> {
                    ScreenshakeHandler.addScreenshake(builder -> builder.setDuration(40).setStrength(0.6f, 0));
                    feedback.accept(context.getSource(), Component.literal("camera should shake for two seconds"));
                    return 1;
                }))
                .then(LiteralArgumentBuilder.<S>literal("screenparticles").executes(context -> {
                    Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new TestScreen(false)));
                    return 1;
                }))
                .then(LiteralArgumentBuilder.<S>literal("armor").executes(context -> {
                    Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new TestScreen(true)));
                    return 1;
                }))
                .then(LiteralArgumentBuilder.<S>literal("pooled").executes(context -> {
                    spawnPooled();
                    feedback.accept(context.getSource(), Component.literal("a cyan ring of 64 pooled billboards should drift upward and disappear after five seconds"));
                    return 1;
                }))
                .then(LiteralArgumentBuilder.<S>literal("particles").executes(context -> {
                    spawnStandard();
                    return 1;
                }))
                .then(LiteralArgumentBuilder.<S>literal("transparency").executes(context -> {
                    spawnTransparency();
                    feedback.accept(context.getSource(), Component.literal("left: additive wisps, right: lumitransparent wisps - check transparency"));
                    return 1;
                }))
                .then(LiteralArgumentBuilder.<S>literal("worldeventclient").executes(context -> {
                    int count = 0;
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.level != null) {
                        for (WorldEventInstance instance : WorldEventStorageAccess.get(minecraft.level).activeWorldEvents) {
                            if (instance instanceof TestWorldEvent event) {
                                feedback.accept(context.getSource(), Component.literal("client beacon " + event.uuid + " age=" + event.age + "/" + event.duration));
                                count++;
                            }
                        }
                    }
                    feedback.accept(context.getSource(), Component.literal("client test events: " + count));
                    return count;
                }))
                .then(LiteralArgumentBuilder.<S>literal("reload").executes(context -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    LodestoneArmorModel oldModel = ARMOR.getModel(minecraft.getEntityModels());
                    Object oldParticleShader = LodestoneShaders.PARTICLE.getShaderInstance();
                    Object oldPooledShader = POOLED_SHADER.getShaderInstance();
                    minecraft.reloadResourcePacks().whenComplete((ignored, error) -> minecraft.execute(() -> {
                        if (error != null) {
                            feedback.accept(context.getSource(), Component.literal("FAIL: resource reload: " + error.getMessage()));
                            return;
                        }
                        boolean rebaked = oldModel != ARMOR.getModel(minecraft.getEntityModels());
                        boolean particleReloaded = oldParticleShader != LodestoneShaders.PARTICLE.getShaderInstance();
                        boolean pooledReloaded = oldPooledShader != POOLED_SHADER.getShaderInstance();
                        feedback.accept(context.getSource(), Component.literal("reload: armor rebaked=" + rebaked + ", particle shader replaced=" + particleReloaded + ", pooled shader replaced=" + pooledReloaded));
                    }));
                    return 1;
                }));
        tests.then(LiteralArgumentBuilder.<S>literal("status").executes(context -> {
            int live = ParticleHandler.allPoolGroups().stream().flatMap(group -> group.pools().stream()).mapToInt(pool -> pool.count()).sum();
            feedback.accept(context.getSource(), Component.literal("pooled live=" + live + ", model bakes=" + modelBakes
                    + ", pooled shader loaded=" + (POOLED_SHADER.getShaderInstance() != null)));
            return 1;
        }));
        tests.then(LiteralArgumentBuilder.<S>literal("bloom").executes(context -> {
            BloomPostProcessor bloom = LodestonePostProcessEffects.BLOOM;
            if (bloom.getBloomTarget() == null) {
                feedback.accept(context.getSource(), Component.literal("bloom is unavailable: its post-processing shader failed to load"));
                return 0;
            }
            bloom.setActive(true);
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null || minecraft.player == null) {
                return 0;
            }
            LodestoneRenderType type = LodestoneRenderTypes.createGenericRenderType(
                    RenderTypeToken.createToken(TextureAtlas.LOCATION_PARTICLES), "test_bloom",
                    DefaultVertexFormat.PARTICLE, Mode.QUADS,
                    builder -> builder.copyState(LodestoneRenderTypes.ADDITIVE_PARTICLE.state).setOutputState(bloom.getBloomOutput()));
            LodestoneWorldParticleRenderType particles =
                    new LodestoneWorldParticleRenderType(
                            type, LodestoneShaders.PARTICLE, TextureAtlas.LOCATION_PARTICLES,
                            SourceFactor.SRC_ALPHA, DestFactor.ONE);
            Vec3 position = minecraft.player.getEyePosition().add(minecraft.player.getLookAngle().scale(4));
            WorldParticleBuilder.create(LodestoneParticles.WISP_PARTICLE).setRenderType(particles).setLifetime(100).setForceSpawn(true)
                    .setScaleData(GenericParticleData.create(0.5f, 0).build()).setRandomOffset(0.5).repeat(minecraft.level, position, 20);
            feedback.accept(context.getSource(), Component.literal("expected: a bright glowing particle cluster for five seconds. /lode tests bloomoff disables bloom"));
            return 1;
        }));
        tests.then(LiteralArgumentBuilder.<S>literal("bloomoff").executes(context -> {
            LodestonePostProcessEffects.BLOOM.setActive(false);
            return 1;
        }));
        dispatcher.register(LiteralArgumentBuilder.<S>literal("lode").then(tests)
                .then(ParticleTestCommand.register(feedback, feedback)));
        dispatcher.register(LiteralArgumentBuilder.<S>literal("lodestone").redirect(dispatcher.getRoot().getChild("lode")));
        dispatcher.register(LiteralArgumentBuilder.<S>literal("lodec").then(tests));
    }

    public static void spawnStandard() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        Vec3 center = minecraft.player.getEyePosition().add(minecraft.player.getLookAngle().scale(4));
        for (int i = 0; i < 48; i++) {
            double angle = i * Math.PI * 2 / 48;
            WorldParticleBuilder.create(LodestoneParticles.WISP_PARTICLE)
                    .setLifetime(100).setForceSpawn(true)
                    .setScaleData(GenericParticleData.create(0.25f, 0).build())
                    .setMotion(0, 0.005, 0)
                    .spawn(minecraft.level, center.add(Math.cos(angle), Math.sin(angle), 0));
        }
    }

    public static void spawnPooled() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        ParticleSpec spec = pooledSpec();
        Vec3 center = minecraft.player.getEyePosition().add(minecraft.player.getLookAngle().scale(4));
        for (int i = 0; i < 64; i++) {
            double angle = i * Math.PI * 2 / 64;
            ParticleSpawnContext spawn = new ParticleSpawnContext().position(center.x + Math.cos(angle), center.y + Math.sin(angle), center.z)
                    .motion(0, 0.004, 0).scale(0.3f, 0.3f, 0.3f).color(0.2f, 0.9f, 1, 1).lifetime(100);
            ParticleHandler.spawn(spec, spawn);
        }
    }

    public static ParticleSpec pooledSpec() {
        if (pooledType == null) {
            pooledType = LodestoneRenderTypes.createGenericRenderType(RenderTypeToken.createToken(ResourceLocation.parse("lodestone:textures/particle/wisp.png")),
                    "test_pooled", DefaultVertexFormat.POSITION_TEX, Mode.QUADS,
                    builder -> builder.copyState(LodestoneRenderTypes.TRANSPARENT_PARTICLE.state)
                            .setTextureState(ResourceLocation.parse("lodestone:textures/particle/wisp.png"))
                            .setShaderState(POOLED_SHADER));
        }
        return ParticleBuilder.create().withVisual(LodestoneParticleVisuals.BILLBOARD, config -> config.renderType(pooledType)).build();
    }

    private static void spawnTransparency() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        Vec3 forward = minecraft.player.getLookAngle();
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = right.cross(forward).normalize();
        Vec3 center = minecraft.player.getEyePosition().add(forward.scale(4));
        for (int ring = 0; ring < 2; ring++) {
            Vec3 offset = right.scale(ring == 0 ? -1.2 : 1.2);
            for (int i = 0; i < 32; i++) {
                double angle = i * Math.PI * 2 / 32;
                Vec3 position = center.add(offset).add(right.scale(Math.cos(angle) * 0.7)).add(up.scale(Math.sin(angle) * 0.7));
                WorldParticleBuilder.create(LodestoneParticles.WISP_PARTICLE)
                        .setRenderType(ring == 0 ? LodestoneWorldParticleRenderType.ADDITIVE : LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                        .setLifetime(200).setForceSpawn(true)
                        .setScaleData(GenericParticleData.create(0.2f).build())
                        .spawn(minecraft.level, position);
            }
        }
    }
}
