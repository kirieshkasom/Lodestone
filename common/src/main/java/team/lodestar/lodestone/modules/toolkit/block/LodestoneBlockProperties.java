package team.lodestar.lodestone.modules.toolkit.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.modules.core.datagen.DatagenOnly;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.core.util.BlockItemTagKey;

import java.util.function.*;

/**
 * An extension of Block Properties designed to interface with {@link LodestoneDatagenBlockData}
 * Some of these values are optional; they only get added during datagen.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class LodestoneBlockProperties extends BlockBehaviour.Properties {

    public enum BlockRenderType {
        SOLID("solid"),
        CUTOUT("cutout"),
        CUTOUT_MIPPED("cutout_mipped"),
        CUTOUT_MIPPED_ALL("cutout_mipped_all"),
        TRANSLUCENT("translucent"),
        TRIPWIRE("tripwire");

        private final ResourceLocation location;

        BlockRenderType(String name) {
            this.location = ResourceLocation.withDefaultNamespace(name);
        }

        public ResourceLocation getLocation() {
            return location;
        }
    }

    public LodestoneBlockProperties() {
        super();
    }

    public static LodestoneBlockProperties of() {
        return new LodestoneBlockProperties();
    }

    public LodestoneBlockProperties copy() {
        return copy(this);
    }

    public static LodestoneBlockProperties copy(BlockBehaviour behaviour) {
        return copy(behaviour.properties);
    }

    public static LodestoneBlockProperties copy(BlockBehaviour.Properties properties) {
        LodestoneBlockProperties copy = LodestoneBlockProperties.of();
        copy.destroyTime = properties.destroyTime;
        copy.explosionResistance = properties.explosionResistance;
        copy.hasCollision = properties.hasCollision;
        copy.isRandomlyTicking = properties.isRandomlyTicking;
        copy.lightEmission = properties.lightEmission;
        copy.mapColor = properties.mapColor;
        copy.soundType = properties.soundType;
        copy.friction = properties.friction;
        copy.speedFactor = properties.speedFactor;
        copy.dynamicShape = properties.dynamicShape;
        copy.canOcclude = properties.canOcclude;
        copy.isAir = properties.isAir;
        copy.requiresCorrectToolForDrops = properties.requiresCorrectToolForDrops;
        copy.jumpFactor = properties.jumpFactor;
        copy.drops = properties.drops;
        copy.ignitedByLava = properties.ignitedByLava;
        copy.forceSolidOn = properties.forceSolidOn;
        copy.pushReaction = properties.pushReaction;
        copy.spawnTerrainParticles = properties.spawnTerrainParticles;
        copy.instrument = properties.instrument;
        copy.replaceable = properties.replaceable;
        copy.isValidSpawn = properties.isValidSpawn;
        copy.isRedstoneConductor = properties.isRedstoneConductor;
        copy.isSuffocating = properties.isSuffocating;
        copy.isViewBlocking = properties.isViewBlocking;
        copy.emissiveRendering = properties.emissiveRendering;
        copy.requiredFeatures = properties.requiredFeatures;
        copy.offsetFunction = properties.offsetFunction;
        copy.hasPostProcess = properties.hasPostProcess;

        if (properties instanceof LodestoneBlockProperties from) {
            from.copyDatagenDataTo(copy);
        }

        return copy;
    }


    public LodestoneBlockProperties offsetFunction(BlockBehaviour.OffsetFunction offsetFunction) {
        this.offsetFunction = offsetFunction;
        return this;
    }

    @Override
    @NotNull
    public LodestoneBlockProperties offsetType(@NotNull BlockBehaviour.OffsetType pOffsetType) {
        return (LodestoneBlockProperties) super.offsetType(pOffsetType);
    }

    @DatagenOnly
    public LodestoneBlockProperties addDatagenData(Consumer<LodestoneDatagenBlockData> function) {
        if (DatagenModLoader.isRunningDataGen()) {
            function.accept(getDatagenData());
        }
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties copyDatagenDataTo(LodestoneBlockProperties to) {
        if (DatagenModLoader.isRunningDataGen()) {
            LodestoneDatagenBlockData.copyDatagenDataFrom(this, to);
        }
        return this;
    }

    @DatagenOnly
    public LodestoneDatagenBlockData getDatagenData() {
        return LodestoneDatagenBlockData.getDatagenData(this);
    }

    @DatagenOnly
    public LodestoneBlockProperties addTag(TagKey<Block> tag) {
        addDatagenData(d -> d.addTag(tag));
        return this;
    }

    @DatagenOnly
    @SafeVarargs
    public final LodestoneBlockProperties addTags(TagKey<Block>... tags) {
        addDatagenData(d -> d.addTags(tags));
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties addTag(BlockItemTagKey tag) {
        addDatagenData(d -> d.addTag(tag));
        return this;
    }

    @DatagenOnly
    public final LodestoneBlockProperties addTags(BlockItemTagKey... tags) {
        addDatagenData(d -> d.addTags(tags));
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties noLootDatagen() {
        addDatagenData(LodestoneDatagenBlockData::noLootDatagen);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsPickaxe() {
        addDatagenData(LodestoneDatagenBlockData::needsPickaxe);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsAxe() {
        addDatagenData(LodestoneDatagenBlockData::needsAxe);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsShovel() {
        addDatagenData(LodestoneDatagenBlockData::needsShovel);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsHoe() {
        addDatagenData(LodestoneDatagenBlockData::needsHoe);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsStone() {
        addDatagenData(LodestoneDatagenBlockData::needsStone);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsIron() {
        addDatagenData(LodestoneDatagenBlockData::needsIron);
        return this;
    }

    @DatagenOnly
    public LodestoneBlockProperties needsDiamond() {
        addDatagenData(LodestoneDatagenBlockData::needsDiamond);
        return this;
    }

    public LodestoneBlockProperties setCutout() {
        return setRenderType(BlockRenderType.CUTOUT);
    }

    public LodestoneBlockProperties setTranslucent() {
        return setRenderType(BlockRenderType.TRANSLUCENT);
    }

    public LodestoneBlockProperties setRenderType(BlockRenderType renderType) {
        addDatagenData(d -> d.setRenderType(renderType));
        return this;
    }

    @Override
    @NotNull
    public LodestoneBlockProperties noCollission() {
        return (LodestoneBlockProperties) super.noCollission();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties noOcclusion() {
        return (LodestoneBlockProperties) super.noOcclusion();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties friction(float friction) {
        return (LodestoneBlockProperties) super.friction(friction);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties speedFactor(float factor) {
        return (LodestoneBlockProperties) super.speedFactor(factor);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties jumpFactor(float factor) {
        return (LodestoneBlockProperties) super.jumpFactor(factor);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties sound(@NotNull SoundType type) {
        return (LodestoneBlockProperties) super.sound(type);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties lightLevel(@NotNull ToIntFunction<BlockState> lightMap) {
        return (LodestoneBlockProperties) super.lightLevel(lightMap);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties strength(float destroyTime, float explosionResistance) {
        return (LodestoneBlockProperties) super.strength(destroyTime, explosionResistance);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties instabreak() {
        return (LodestoneBlockProperties) super.instabreak();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties strength(float strength) {
        return (LodestoneBlockProperties) super.strength(strength);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties randomTicks() {
        return (LodestoneBlockProperties) super.randomTicks();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties dynamicShape() {
        return (LodestoneBlockProperties) super.dynamicShape();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties noLootTable() {
        noLootDatagen();
        return (LodestoneBlockProperties) super.noLootTable();
    }

    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public LodestoneBlockProperties dropsLike(@NotNull Block block) {
        noLootDatagen();
        return (LodestoneBlockProperties) super.dropsLike(block);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties lootFrom(@NotNull Supplier<? extends Block> blockIn) {
        noLootDatagen();
        return (LodestoneBlockProperties) super.lootFrom(blockIn);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties air() {
        return (LodestoneBlockProperties) super.air();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties isValidSpawn(@NotNull BlockBehaviour.StateArgumentPredicate<EntityType<?>> predicate) {
        return (LodestoneBlockProperties) super.isValidSpawn(predicate);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties isRedstoneConductor(@NotNull BlockBehaviour.StatePredicate predicate) {
        return (LodestoneBlockProperties) super.isRedstoneConductor(predicate);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties isSuffocating(@NotNull BlockBehaviour.StatePredicate predicate) {
        return (LodestoneBlockProperties) super.isSuffocating(predicate);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties isViewBlocking(@NotNull BlockBehaviour.StatePredicate predicate) {
        return (LodestoneBlockProperties) super.isViewBlocking(predicate);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties hasPostProcess(@NotNull BlockBehaviour.StatePredicate predicate) {
        return (LodestoneBlockProperties) super.hasPostProcess(predicate);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties emissiveRendering(@NotNull BlockBehaviour.StatePredicate predicate) {
        return (LodestoneBlockProperties) super.emissiveRendering(predicate);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties requiresCorrectToolForDrops() {
        return (LodestoneBlockProperties) super.requiresCorrectToolForDrops();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties mapColor(@NotNull Function<BlockState, MapColor> p_285406_) {
        return (LodestoneBlockProperties) super.mapColor(p_285406_);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties mapColor(@NotNull DyeColor p_285331_) {
        return (LodestoneBlockProperties) super.mapColor(p_285331_);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties mapColor(@NotNull MapColor p_285137_) {
        return (LodestoneBlockProperties) super.mapColor(p_285137_);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties destroyTime(float destroyTime) {
        return (LodestoneBlockProperties) super.destroyTime(destroyTime);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties explosionResistance(float explosionResistance) {
        return (LodestoneBlockProperties) super.explosionResistance(explosionResistance);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties ignitedByLava() {
        return (LodestoneBlockProperties) super.ignitedByLava();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties liquid() {
        return (LodestoneBlockProperties) super.liquid();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties forceSolidOn() {
        return (LodestoneBlockProperties) super.forceSolidOn();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties pushReaction(@NotNull PushReaction p_278265_) {
        return (LodestoneBlockProperties) super.pushReaction(p_278265_);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties noTerrainParticles() {
        return (LodestoneBlockProperties) super.noTerrainParticles();
    }

    @Override
    @NotNull
    public LodestoneBlockProperties requiredFeatures(FeatureFlag @NotNull ... pRequiredFeatures) {
        return (LodestoneBlockProperties) super.requiredFeatures(pRequiredFeatures);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties instrument(@NotNull NoteBlockInstrument p_282170_) {
        return (LodestoneBlockProperties) super.instrument(p_282170_);
    }

    @Override
    @NotNull
    public LodestoneBlockProperties replaceable() {
        return (LodestoneBlockProperties) super.replaceable();
    }
}