package team.lodestar.lodestone.modules.toolkit.block;

import net.minecraft.core.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public class VoxelShapeRotator {

    protected final HashMap<Direction, VoxelShape> variants;

    public VoxelShapeRotator(VoxelShape baseShape) {
        variants = new HashMap<>();
        variants.put(Direction.UP, baseShape);
    }

    public VoxelShape getShape(BlockState state) {
        var property = getDirectionProperty(state.getBlock());
        var direction = state.getValue(property);
        if (!variants.containsKey(direction)) {
            variants.put(direction, rotateShape(direction, variants.get(Direction.UP)));
        }

        return variants.get(direction);
    }

    public Collection<Direction> getPossibleDirections(Block block) {
        return getDirectionProperty(block).getPossibleValues();
    }

    public DirectionProperty getDirectionProperty(Block block) {
        var state = block.defaultBlockState();
        for (Property<?> value : state.getValues().keySet()) {
            if (value instanceof DirectionProperty directionProperty) {
                return directionProperty;
            }
        }
        throw new UnsupportedOperationException("Block that utilizes a Voxel Shape Rotator does not carry a facing property.");
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static VoxelShape rotateShape(Direction direction, VoxelShape baseShape) {
        var rotated = new AtomicReference<>(Shapes.empty());
        baseShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double rotatedMinX = minX;
            double rotatedMinY = minY;
            double rotatedMinZ = minZ;
            double rotatedMaxX = maxX;
            double rotatedMaxY = maxY;
            double rotatedMaxZ = maxZ;

            switch (direction) {
                case DOWN -> {
                    rotatedMinY = 1 - maxY;
                    rotatedMaxY = 1 - minY;
                }

                case NORTH -> {
                    rotatedMinZ = 1 - maxY;
                    rotatedMaxZ = 1 - minY;
                    rotatedMinY = minZ;
                    rotatedMaxY = maxZ;
                }

                case SOUTH -> {
                    rotatedMinY = 1 - maxZ;
                    rotatedMaxY = 1 - minZ;
                    rotatedMinZ = minY;
                    rotatedMaxZ = maxY;
                }

                case WEST -> {
                    rotatedMinX = 1 - maxY;
                    rotatedMaxX = 1 - minY;
                    rotatedMinY = minX;
                    rotatedMaxY = maxX;
                }

                case EAST -> {
                    rotatedMinY = 1 - maxX;
                    rotatedMaxY = 1 - minX;
                    rotatedMinX = minY;
                    rotatedMaxX = maxY;
                }
            }

            VoxelShape rotatedBox = Shapes.box(
                    rotatedMinX, rotatedMinY, rotatedMinZ,
                    rotatedMaxX, rotatedMaxY, rotatedMaxZ
            );

            rotated.set(Shapes.join(rotated.get(), rotatedBox, BooleanOp.OR));
        });

        return rotated.get();
    }
}