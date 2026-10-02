package team.lodestar.lodestone.modules.toolkit.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LodestoneLogBlock extends RotatedPillarBlock {
    @Nullable
    public final Supplier<Block> stripped;

    public LodestoneLogBlock(Properties properties, @Nullable Supplier<Block> stripped) {
        super(properties);
        this.stripped = stripped;
    }

    public LodestoneLogBlock(Properties properties) {
        this(properties, null);
    }

    public @Nullable BlockState getStrippedState(BlockState state) {
        if (stripped == null) {
            return null;
        }
        return stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
    }
}
