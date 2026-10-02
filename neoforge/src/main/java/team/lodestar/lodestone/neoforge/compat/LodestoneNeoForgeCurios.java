package team.lodestar.lodestone.neoforge.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import team.lodestar.lodestone.compability.CuriosCompat;
import team.lodestar.lodestone.helpers.CurioHelper;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class LodestoneNeoForgeCurios {
    private LodestoneNeoForgeCurios() {
    }

    public static void install() {
        boolean loaded = ModList.get().isLoaded("curios");
        if (loaded) {
            LoadedOnly.install();
        }
        CuriosCompat.init(loaded);
    }

    private static final class LoadedOnly {
        private static void install() {
            CurioHelper.install(new CurioHelper.Provider() {
                @Override
                public List<CurioHelper.EquippedCurio> equipped(LivingEntity entity) {
                    return CuriosApi.getCuriosInventory(entity).map(handler -> handler.findCurios(stack -> true).stream()
                            .map(result -> new CurioHelper.EquippedCurio(result.slotContext().identifier(), result.slotContext().index(), result.stack()))
                            .toList()).orElseGet(List::of);
                }

                @Override
                public Optional<ImmutableTriple<String, Integer, ItemStack>> cosmetic(Predicate<ItemStack> filter, LivingEntity entity) {
                    return NeoForgeCurioHelper.findCosmeticCurio(filter, entity);
                }
            });
        }
    }
}
