package team.lodestar.lodestone.modules.datagen.providers.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.Direction;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.IDatagenPathfinder;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import team.lodestar.lodestone.modules.datagen.model.ConfiguredModel;
import team.lodestar.lodestone.modules.datagen.providers.LodestoneJsonDataProvider;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelSystem;
import team.lodestar.lodestone.modules.datagen.smith.blockstate.ModularBlockStateSmith;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public abstract class LodestoneBlockStateSystem extends LodestoneJsonDataProvider implements IDatagenPathfinder {
    private final String modId;
    private final LodestoneBlockModelProvider blockModels;
    public final LodestoneItemModelSystem itemModelProvider;

    public LodestoneBlockStateSystem(PackOutput output, String modId, LodestoneItemModelSystem itemModelProvider) {
        super(output, PackOutput.Target.RESOURCE_PACK, "blockstates", modId + " Block States");
        this.modId = modId;
        this.itemModelProvider = itemModelProvider;
        this.blockModels = new LodestoneBlockModelProvider(output, modId);
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public String getFolder() {
        return "";
    }

    public LodestoneBlockModelProvider models() {
        return blockModels;
    }

    public LodestoneItemModelSystem itemModels() {
        return itemModelProvider;
    }

    public void setTexturePath(String folder) {
        DatagenSystemCommons.BLOCK_TEXTURE.setFolder(folder);
    }

    public ModularBlockStateSmith.ModelFileSupplier fromFunction(BiFunction<String, ResourceLocation, ModelFile> modelFileFunction) {
        return block -> modelFileFunction.apply(getBlockName(block), getBlockTexture(getBlockName(block)));
    }

    protected abstract void registerStatesAndModels();

    public void simpleBlock(Block block) {
        simpleBlock(block, blockModels.cubeAll(getBlockName(block), getBlockTexture(block)));
    }

    public void simpleBlock(Block block, ModelFile model) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model.getLocation().toString());
        JsonObject variants = new JsonObject();
        variants.add("", variant);
        JsonObject root = new JsonObject();
        root.add("variants", variants);
        save(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block), root);
    }

    private String stateKey(BlockState state) {
        StringBuilder key = new StringBuilder();
        for (Property<?> property : state.getProperties()) {
            if (key.length() > 0) {
                key.append(',');
            }
            key.append(property.getName()).append('=').append(propertyValue(state, property));
        }
        return key.toString();
    }

    private <T extends Comparable<T>> String propertyValue(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private void writeAllStates(Block block, java.util.function.Function<BlockState, ConfiguredModel> function) {
        writeAllStatesExcept(block, function);
    }

    private void writeAllStatesExcept(Block block, java.util.function.Function<BlockState, ConfiguredModel> function, Property<?>... ignoredProperties) {
        JsonObject variants = new JsonObject();
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            StringBuilder key = new StringBuilder();
            for (Property<?> property : state.getProperties()) {
                boolean ignored = false;
                for (Property<?> ignoredProperty : ignoredProperties) {
                    if (property == ignoredProperty) {
                        ignored = true;
                        break;
                    }
                }
                if (!ignored) {
                    if (key.length() > 0) {
                        key.append(',');
                    }
                    key.append(property.getName()).append('=').append(propertyValue(state, property));
                }
            }
            variants.add(key.toString(), configuredModelJson(function.apply(state), false));
        }
        JsonObject root = new JsonObject(); root.add("variants", variants);
        save(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block), root);
    }

    private JsonObject multipartModel(ModelFile model, int rotationY, boolean uvLock) {
        JsonObject when = new JsonObject();
        JsonObject apply = new JsonObject(); apply.addProperty("model", model.getLocation().toString());
        if (rotationY != 0) {
            apply.addProperty("y", rotationY);
        }
        if (uvLock) {
            apply.addProperty("uvlock", true);
        }
        JsonObject part = new JsonObject(); part.add("apply", apply);
        return part;
    }

    private void addMultipartPart(JsonArray parts, ModelFile model, int rotationY, boolean uvLock, String property, String value) {
        JsonObject part = multipartModel(model, rotationY, uvLock);
        if (property != null) {
            JsonObject when = new JsonObject(); when.addProperty(property, value); part.add("when", when);
        }
        parts.add(part);
    }

    private void saveMultipart(Block block, JsonArray parts) {
        JsonObject root = new JsonObject(); root.add("multipart", parts);
        save(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block), root);
    }

    public void simpleBlock(Block block, ConfiguredModel... models) {
        if (models.length == 0) {
            throw new IllegalArgumentException("At least one configured model is required");
        }
        JsonObject variants = new JsonObject();
        JsonArray entries = new JsonArray();
        for (int index = 0; index < models.length; index++) {
            ConfiguredModel model = models[index];
            entries.add(configuredModelJson(model, models.length > 1));
        }
        JsonObject root = new JsonObject();
        variants.add("", models.length == 1 ? entries.get(0) : entries);
        root.add("variants", variants);
        save(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block), root);
    }

    private JsonObject configuredModelJson(ConfiguredModel model, boolean includeWeight) {
        JsonObject json = new JsonObject();
        json.addProperty("model", model.model().getLocation().toString());
        if (model.x() != 0) {
            json.addProperty("x", model.x());
        }
        if (model.y() != 0) {
            json.addProperty("y", model.y());
        }
        if (model.uvLock()) {
            json.addProperty("uvlock", true);
        }
        if (includeWeight && model.weight() != 1) {
            json.addProperty("weight", model.weight());
        }
        return json;
    }

    public VariantBuilder getVariantBuilder(Block block) {
        return new VariantBuilder(block);
    }

    public void horizontalBlock(Block block, ModelFile model) {
        horizontalBlock(block, model, 180);
    }

    public void horizontalBlock(Block block, ModelFile model, int rotationOffset) {
        writeAllStates(block, state -> ConfiguredModel.builder().modelFile(model).rotationY(((int) state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).toYRot() + rotationOffset) % 360).build());
    }

    public void directionalBlock(Block block, ModelFile model) {
        writeAllStates(block, state -> {
            Direction direction = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
            int rotationX = direction == Direction.DOWN ? 180 : direction.getAxis().isHorizontal() ? 90 : 0;
            int rotationY = direction.getAxis().isVertical() ? 0 : (((int) direction.toYRot()) + 180) % 360;
            return ConfiguredModel.builder().modelFile(model).rotationX(rotationX).rotationY(rotationY).build();
        });
    }

    public void stairsBlock(StairBlock block, ResourceLocation texture) {
        String name = getBlockName(block);
        ModelFile straight = models().stairs(name, texture, texture, texture);
        ModelFile inner = models().stairsInner(name + "_inner", texture, texture, texture);
        ModelFile outer = models().stairsOuter(name + "_outer", texture, texture, texture);
        writeAllStatesExcept(block, state -> {
            net.minecraft.core.Direction facing = state.getValue(StairBlock.FACING);
            net.minecraft.world.level.block.state.properties.Half half = state.getValue(StairBlock.HALF);
            net.minecraft.world.level.block.state.properties.StairsShape shape = state.getValue(StairBlock.SHAPE);
            int yRot = (int) facing.getClockWise().toYRot();
            if (shape == net.minecraft.world.level.block.state.properties.StairsShape.INNER_LEFT || shape == net.minecraft.world.level.block.state.properties.StairsShape.OUTER_LEFT) {
                yRot += 270;
            }
            if (shape != net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT && half == net.minecraft.world.level.block.state.properties.Half.TOP) {
                yRot += 90;
            }
            yRot %= 360;
            boolean uvlock = yRot != 0 || half == net.minecraft.world.level.block.state.properties.Half.TOP;
            ModelFile selected = shape == net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT ? straight : shape == net.minecraft.world.level.block.state.properties.StairsShape.INNER_LEFT || shape == net.minecraft.world.level.block.state.properties.StairsShape.INNER_RIGHT ? inner : outer;
            return ConfiguredModel.builder().modelFile(selected).rotationX(half == net.minecraft.world.level.block.state.properties.Half.BOTTOM ? 0 : 180).rotationY(yRot).uvLock(uvlock).build();
        }, StairBlock.WATERLOGGED);
    }

    public void slabBlock(SlabBlock block, ResourceLocation doubleModel, ResourceLocation texture) {
        String name = getBlockName(block);
        ModelFile bottom = models().slab(name, texture, texture, texture);
        ModelFile top = models().slabTop(name + "_top", texture, texture, texture);
        ModelFile doubled = models().getExistingFile(doubleModel);
        writeAllStatesExcept(block, state -> {
            net.minecraft.world.level.block.state.properties.SlabType type = state.getValue(SlabBlock.TYPE);
            return ConfiguredModel.builder().modelFile(type == net.minecraft.world.level.block.state.properties.SlabType.TOP ? top : type == net.minecraft.world.level.block.state.properties.SlabType.DOUBLE ? doubled : bottom).build();
        }, SlabBlock.WATERLOGGED);
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        JsonArray parts = new JsonArray();
        ModelFile post = models().wallPost(name + "_post", texture);
        ModelFile side = models().wallSide(name + "_side", texture);
        ModelFile tall = models().wallSideTall(name + "_side_tall", texture);
        addMultipartPart(parts, post, 0, false, "up", "true");
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            String key = direction.getName();
            int rotation = ((int) direction.toYRot() + 180) % 360;
            addMultipartPart(parts, side, rotation, true, key, "low");
            addMultipartPart(parts, tall, rotation, true, key, "tall");
        }
        saveMultipart(block, parts);
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        JsonArray parts = new JsonArray();
        addMultipartPart(parts, models().fencePost(name + "_post", texture), 0, false, null, null);
        ModelFile side = models().fenceSide(name + "_side", texture);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            addMultipartPart(parts, side, ((int) direction.toYRot() + 180) % 360, true, direction.getName(), "true");
        }
        saveMultipart(block, parts);
    }

    public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        ModelFile closed = models().fenceGate(name, texture);
        ModelFile open = models().fenceGateOpen(name + "_open", texture);
        ModelFile wall = models().fenceGateWall(name + "_wall", texture);
        ModelFile wallOpen = models().fenceGateWallOpen(name + "_wall_open", texture);
        writeAllStatesExcept(block, state -> {
            ModelFile model = state.getValue(FenceGateBlock.IN_WALL) ? wall : closed;
            if (state.getValue(FenceGateBlock.OPEN)) {
                model = model == wall ? wallOpen : open;
            }
            return ConfiguredModel.builder().modelFile(model).rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot()).uvLock(true).build();
        }, FenceGateBlock.POWERED);
    }

    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
        String name = getBlockName(block);
        ModelFile up = models().pressurePlate(name, texture);
        ModelFile down = models().pressurePlateDown(name + "_down", texture);
        writeAllStates(block, state -> ConfiguredModel.builder().modelFile(state.getValue(PressurePlateBlock.POWERED) ? down : up).build());
    }

    public void buttonBlock(ButtonBlock block, ResourceLocation texture) {
        String name = getBlockName(block);
        ModelFile up = models().button(name, texture);
        ModelFile down = models().buttonPressed(name + "_pressed", texture);
        writeAllStates(block, state -> {
            net.minecraft.core.Direction facing = state.getValue(ButtonBlock.FACING);
            net.minecraft.world.level.block.state.properties.AttachFace face = state.getValue(ButtonBlock.FACE);
            boolean powered = state.getValue(ButtonBlock.POWERED);
            int rotationX = face == net.minecraft.world.level.block.state.properties.AttachFace.FLOOR ? 0 : face == net.minecraft.world.level.block.state.properties.AttachFace.WALL ? 90 : 180;
            int rotationY = (int) (face == net.minecraft.world.level.block.state.properties.AttachFace.CEILING ? facing : facing.getOpposite()).toYRot();
            return ConfiguredModel.builder().modelFile(powered ? down : up).rotationX(rotationX).rotationY(rotationY).uvLock(face == net.minecraft.world.level.block.state.properties.AttachFace.WALL).build();
        });
    }

    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        ModelFile bottomLeft = models().door(name + "_bottom_left", "block/door_bottom_left", bottom, top);
        ModelFile bottomLeftOpen = models().door(name + "_bottom_left_open", "block/door_bottom_left_open", bottom, top);
        ModelFile bottomRight = models().door(name + "_bottom_right", "block/door_bottom_right", bottom, top);
        ModelFile bottomRightOpen = models().door(name + "_bottom_right_open", "block/door_bottom_right_open", bottom, top);
        ModelFile topLeft = models().door(name + "_top_left", "block/door_top_left", bottom, top);
        ModelFile topLeftOpen = models().door(name + "_top_left_open", "block/door_top_left_open", bottom, top);
        ModelFile topRight = models().door(name + "_top_right", "block/door_top_right", bottom, top);
        ModelFile topRightOpen = models().door(name + "_top_right_open", "block/door_top_right_open", bottom, top);
        writeAllStatesExcept(block, state -> {
            int yRot = (int) state.getValue(DoorBlock.FACING).toYRot() + 90;
            boolean right = state.getValue(DoorBlock.HINGE) == net.minecraft.world.level.block.state.properties.DoorHingeSide.RIGHT;
            boolean open = state.getValue(DoorBlock.OPEN);
            boolean lower = state.getValue(DoorBlock.HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
            if (open) {
                yRot += 90;
            }
            if (right && open) {
                yRot += 180;
            }
            yRot %= 360;
            ModelFile model = lower ? right ? open ? bottomRightOpen : bottomRight : open ? bottomLeftOpen : bottomLeft
                    : right ? open ? topRightOpen : topRight : open ? topLeftOpen : topLeft;
            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        }, DoorBlock.POWERED);
    }

    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
        String prefix = orientable ? "block/template_orientable_trapdoor_" : "block/template_trapdoor_";
        ModelFile bottom = models().trapdoor(name + "_bottom", prefix + "bottom", texture);
        ModelFile top = models().trapdoor(name + "_top", prefix + "top", texture);
        ModelFile open = models().trapdoor(name + "_open", prefix + "open", texture);
        writeAllStatesExcept(block, state -> {
            int xRot = 0;
            int yRot = (int) state.getValue(TrapDoorBlock.FACING).toYRot() + 180;
            boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
            if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == net.minecraft.world.level.block.state.properties.Half.TOP) {
                xRot = 180;
                yRot += 180;
            }
            if (!orientable && !isOpen) {
                yRot = 0;
            }
            yRot %= 360;
            ModelFile model = isOpen ? open : state.getValue(TrapDoorBlock.HALF) == net.minecraft.world.level.block.state.properties.Half.TOP ? top : bottom;
            return ConfiguredModel.builder().modelFile(model).rotationX(xRot).rotationY(yRot).build();
        }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
    }
    public void axisBlock(net.minecraft.world.level.block.RotatedPillarBlock block) {
        ResourceLocation texture = getBlockTexture(block);
        axisBlock(block, texture.withSuffix("_side"), texture.withSuffix("_end"));
    }

    public void logBlock(net.minecraft.world.level.block.RotatedPillarBlock block) {
        ResourceLocation texture = getBlockTexture(block);
        axisBlock(block, texture, texture.withSuffix("_top"));
    }
    public void axisBlock(net.minecraft.world.level.block.RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        String name = getBlockName(block);
        ModelFile vertical = models().cubeColumn(name, side, end);
        ModelFile horizontal = models().cubeColumnHorizontal(name + "_horizontal", side, end);
        writeAllStates(block, state -> {
            Direction.Axis axis = state.getValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS);
            if (axis == Direction.Axis.Y) {
                return ConfiguredModel.builder().modelFile(vertical).build();
            }
            return ConfiguredModel.builder().modelFile(horizontal).rotationX(90).rotationY(axis == Direction.Axis.X ? 90 : 0).build();
        });
    }

    public final class VariantBuilder {
        private final Block block;
        private VariantBuilder(Block block) {
            this.block = block;
        }
        public void forAllStates(java.util.function.Function<BlockState, ConfiguredModel> function) {
            JsonObject variants = new JsonObject();
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                StringBuilder key = new StringBuilder();
                for (Property<?> property : state.getProperties()) {
                if (key.length() > 0) {
                    key.append(',');
                }
                    key.append(property.getName()).append('=').append(propertyValue(state, property));
                }
                variants.add(key.toString(), configuredModelJson(function.apply(state), false));
            }
            JsonObject root = new JsonObject(); root.add("variants", variants);
            save(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block), root);
        }
        private <T extends Comparable<T>> String propertyValue(BlockState state, Property<T> property) {
            return property.getName(state.getValue(property));
        }
    }

    public void varyingRotationBlock(Block block, ModelFile model) {
        simpleBlock(block,
                ConfiguredModel.builder().modelFile(model).build(),
                ConfiguredModel.builder().modelFile(model).rotationY(90).build(),
                ConfiguredModel.builder().modelFile(model).rotationY(180).build(),
                ConfiguredModel.builder().modelFile(model).rotationY(270).build());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        generatedJson.clear();
        blockModels.clearGeneratedModels();
        registerStatesAndModels();
        return CompletableFuture.allOf(blockModels.run(output), super.run(output));
    }

}
