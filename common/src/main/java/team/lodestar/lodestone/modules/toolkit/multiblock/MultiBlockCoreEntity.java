package team.lodestar.lodestone.modules.toolkit.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;

import java.util.ArrayList;

public abstract class MultiBlockCoreEntity extends LodestoneBlockEntity implements IMultiBlockCore {

    ArrayList<BlockPos> componentPositions = new ArrayList<>();

    public final MultiBlockStructure structure;

    public MultiBlockCoreEntity(LodestoneBlockEntityType<?> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.structure = structure;
        setupMultiblock(pos);
    }

    @Override
    public MultiBlockStructure getStructure() {
        return structure;
    }

    @Override
    public ArrayList<BlockPos> getComponentPositions() {
        return componentPositions;
    }

    @Override
    public void onBreak(@Nullable Player player) {
        destroyMultiblock(player, level, worldPosition);
    }
}