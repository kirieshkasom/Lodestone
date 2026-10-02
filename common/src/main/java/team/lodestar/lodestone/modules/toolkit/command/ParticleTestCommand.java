package team.lodestar.lodestone.modules.toolkit.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import team.lodestar.lodestone.modules.rendering.particle.standard.builder.WorldParticleBuilder;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.GenericParticleData;
import team.lodestar.lodestone.modules.rendering.particle.standard.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.behaviors.SparkParticleBehavior;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.options.LodestoneItemCrumbsParticleOptions;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.options.LodestoneTerrainParticleOptions;

import java.util.Locale;
import java.util.function.BiConsumer;

public final class ParticleTestCommand {

    private ParticleTestCommand() {
    }

    public static <S> LiteralArgumentBuilder<S> register(BiConsumer<S, Component> success, BiConsumer<S, Component> failure) {
        LiteralArgumentBuilder<S> command = LiteralArgumentBuilder.literal("particle");
        for (TestParticle particle : TestParticle.values()) {
            command.then(LiteralArgumentBuilder.<S>literal(particle.name().toLowerCase(Locale.ROOT))
                    .executes(context -> spawn(context.getSource(), particle, 20, success, failure))
                    .then(RequiredArgumentBuilder.<S, Integer>argument("count", IntegerArgumentType.integer(1, 256))
                            .executes(context -> spawn(context.getSource(), particle, IntegerArgumentType.getInteger(context, "count"), success, failure))));
        }
        return command;
    }

    private static <S> int spawn(S source, TestParticle particle, int count, BiConsumer<S, Component> success, BiConsumer<S, Component> failure) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (level == null || player == null) {
            failure.accept(source, Component.literal("Join a world to test particles."));
            return 0;
        }
        Vec3 direction = player.getLookAngle();
        Vec3 position = player.getEyePosition().add(direction.scale(3));
        for (int i = 0; i < count; i++) {
            WorldParticleBuilder builder = createBuilder(particle, player, position)
                    .setForceSpawn(true)
                    .setLifetime(40)
                    .setScaleData(GenericParticleData.create(1f, 0).build())
                    .setTransparencyData(GenericParticleData.create(1, 0).build())
//                    .setMotion(direction.scale(0.03))
                    .setRandomMotion(0.02);
//                    .setRandomOffset(0.4);
            if (particle == TestParticle.SPARK || particle == TestParticle.EXTRUDING_SPARK || particle == TestParticle.THIN_EXTRUDING_SPARK) {
                builder.setBehavior(SparkParticleBehavior.sparkBehavior())
                        .setScaleData(GenericParticleData.create(0.05f, 0).build())
                        .setLengthData(GenericParticleData.create(1f, 0).build());
            }
            builder.spawn(level, position);
        }
        success.accept(source, Component.literal("spawned " + count + particle.name().toLowerCase(Locale.ROOT) + " particle"));
        return count;
    }

    private static WorldParticleBuilder createBuilder(TestParticle particle, LocalPlayer player, Vec3 position) {
        return switch (particle) {
            case WISP -> WorldParticleBuilder.create(LodestoneParticles.WISP_PARTICLE);
            case SMOKE -> WorldParticleBuilder.create(LodestoneParticles.SMOKE_PARTICLE);
            case SPARKLE -> WorldParticleBuilder.create(LodestoneParticles.SPARKLE_PARTICLE);
            case TWINKLE -> WorldParticleBuilder.create(LodestoneParticles.TWINKLE_PARTICLE);
            case STAR -> WorldParticleBuilder.create(LodestoneParticles.STAR_PARTICLE);
            case SPARK -> WorldParticleBuilder.create(LodestoneParticles.SPARK_PARTICLE);
            case EXTRUDING_SPARK -> WorldParticleBuilder.create(LodestoneParticles.EXTRUDING_SPARK_PARTICLE);
            case THIN_EXTRUDING_SPARK -> WorldParticleBuilder.create(LodestoneParticles.THIN_EXTRUDING_SPARK_PARTICLE);
            case TERRAIN -> WorldParticleBuilder.create(new LodestoneTerrainParticleOptions(
                    LodestoneParticles.TERRAIN_PARTICLE, Blocks.STONE.defaultBlockState(), BlockPos.containing(position)))
                    .setRenderType(LodestoneWorldParticleRenderType.TERRAIN_SHEET);
            case ITEM -> {
                ItemStack stack = player.getMainHandItem();
                if (stack.isEmpty()) {
                    stack = new ItemStack(Items.STONE);
                }
                yield WorldParticleBuilder.create(new LodestoneItemCrumbsParticleOptions(LodestoneParticles.ITEM_PARTICLE, stack.copy()))
                        .setRenderType(LodestoneWorldParticleRenderType.TERRAIN_SHEET);
            }
        };
    }

    private enum TestParticle {
        WISP, SMOKE, SPARKLE, TWINKLE, STAR, SPARK, EXTRUDING_SPARK, THIN_EXTRUDING_SPARK, TERRAIN, ITEM
    }
}
