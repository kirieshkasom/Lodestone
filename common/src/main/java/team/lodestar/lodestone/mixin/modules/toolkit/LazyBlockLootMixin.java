package team.lodestar.lodestone.mixin.modules.toolkit;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;
import team.lodestar.lodestone.internal.client.BlockRenderTypeAccess;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties.BlockRenderType;

import java.util.function.Supplier;

@Mixin(BlockBehaviour.class)
public abstract class LazyBlockLootMixin implements BlockRenderTypeAccess {
    @Unique
    private BlockRenderType lodestone$renderType = BlockRenderType.SOLID;

    @Override
    public BlockRenderType lodestone$getRenderType() {
        return lodestone$renderType;
    }
    @Unique
    private Supplier<? extends Block> lodestone$lootSource;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void lodestone$saveLootSource(BlockBehaviour.Properties properties, CallbackInfo ci) {
        if (properties instanceof LodestoneBlockProperties lodestoneProperties) {
            lodestone$lootSource = lodestoneProperties.getLootSource();
            lodestone$renderType = lodestoneProperties.getRenderType();
        }
    }

    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
    private void lodestone$resolveLootSource(CallbackInfoReturnable<ResourceKey<LootTable>> cir) {
        if (lodestone$lootSource != null) {
            cir.setReturnValue(lodestone$lootSource.get().getLootTable());
        }
    }
}
