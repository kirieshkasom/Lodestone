package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import team.lodestar.lodestone.internal.client.BlockRenderTypeAccess;

public final class LodestoneFabricBlockRenderLayers {
    private LodestoneFabricBlockRenderLayers() {
    }

    public static void install() {
        for (Block block : BuiltInRegistries.BLOCK) {
            apply(block);
        }
        RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, block) -> apply(block));
    }

    private static void apply(Block block) {
        if (block instanceof BlockRenderTypeAccess access) {
            RenderType renderType = switch (access.lodestone$getRenderType()) {
                case CUTOUT -> RenderType.cutout();
                case CUTOUT_MIPPED, CUTOUT_MIPPED_ALL -> RenderType.cutoutMipped();
                case TRANSLUCENT -> RenderType.translucent();
                case TRIPWIRE -> RenderType.tripwire();
                case SOLID -> null;
            };
            if (renderType != null) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
            }
        }
    }
}
