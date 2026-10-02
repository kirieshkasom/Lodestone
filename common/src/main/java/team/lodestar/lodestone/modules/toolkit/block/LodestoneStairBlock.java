package team.lodestar.lodestone.modules.toolkit.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LodestoneStairBlock extends StairBlock {

    public LodestoneStairBlock(Properties properties) {
        super(Blocks.AIR.defaultBlockState(), properties);
    }

    @Override
    public float getExplosionResistance() {
        return this.explosionResistance;
    }
}
