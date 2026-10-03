package team.lodestar.lodestone.fabric.mixin.gameplay;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.class)
public interface CreativeTabPositionMixin {
    @Mutable
    @Accessor("row")
    void lodestone$row(CreativeModeTab.Row row);

    @Mutable
    @Accessor("column")
    void lodestone$column(int column);
}
