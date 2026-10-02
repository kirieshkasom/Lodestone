package team.lodestar.lodestone.modules.toolkit.worldgen;

import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.*;
import team.lodestar.lodestone.helpers.block.*;

import java.util.function.*;

public class LodestoneWorldgenBuilderEntry {
    protected BlockPos pos;
    protected BlockState state;
    protected PlacementCondition placementCondition;
    protected AdditionalPlacement additionalPlacement;

    protected boolean important;

    public LodestoneWorldgenBuilderEntry(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }

    public BlockPos position() {
        return pos;
    }

    public BlockState blockState() {
        return state;
    }

    /**
     * Changes the position of this entry using a function.
     *
     * @param function The function to apply to the current position.
     */
    public LodestoneWorldgenBuilderEntry changePos(Function<BlockPos, BlockPos> function) {
        return changePos(function.apply(pos));
    }

    /**
     * Changes the position of this entry using a new position.
     *
     * @param pos The new position to set for this entry.
     */
    public LodestoneWorldgenBuilderEntry changePos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    /**
     * Changes the block state of this entry using a function.
     *
     * @param function The function to apply to the current block state.
     */
    public LodestoneWorldgenBuilderEntry changeState(Function<BlockState, BlockState> function) {
        return changeState(function.apply(state));
    }

    /**
     * Changes the block state of this entry.
     *
     * @param state The new block state to set for this entry.
     */
    public LodestoneWorldgenBuilderEntry changeState(BlockState state) {
        this.state = state;
        return this;
    }

    /**
     * Adds a condition to the placement of this entry, which will be checked before placing the block.
     *
     * @param placementCondition The placement condition to add to this entry.
     */
    public LodestoneWorldgenBuilderEntry addPlacementCondition(PlacementCondition placementCondition) {
        this.placementCondition = placementCondition;
        return this;
    }

    /**
     * Adds additional placement behavior to this entry which will be executed after the block is placed.
     *
     * @param additionalPlacement The additional placement to add to this entry.
     */
    public LodestoneWorldgenBuilderEntry addAdditionalPlacement(AdditionalPlacement additionalPlacement) {
        this.additionalPlacement = additionalPlacement;
        return this;
    }

    /**
     * \
     * Marks this entry as important, meaning it will be placed even if a previous layer already placed a block at the same position.
     */
    public LodestoneWorldgenBuilderEntry setImportant() {
        this.important = true;
        return this;
    }

    public boolean isImportant() {
        return important;
    }

    public boolean hasPlacementCondition() {
        return placementCondition != null;
    }

    public boolean hasAdditionalPlacement() {
        return additionalPlacement != null;
    }

    public boolean canPlace(WorldGenLevel level) {
        return placementCondition.canPlace(level, this);
    }

    public boolean tryPlace(WorldGenLevel level) {
        if (canPlace(level)) {
            place(level);
            additionalPlacement.place(level, this);
            return true;
        }
        return false;
    }

    public void place(WorldGenLevel level) {
        place(level, position(), blockState());
    }

    /**
     * Places a block at the specified position. For use with the additional placement.
     *
     * @param level The world to place the block in.
     * @param pos   The position to place the block at.
     * @param state The block state to place at the position.
     */
    public void place(WorldGenLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 19);
        if (level instanceof Level realLevel) {
            BlockStateHelper.updateState(realLevel, pos);
        }
    }
}