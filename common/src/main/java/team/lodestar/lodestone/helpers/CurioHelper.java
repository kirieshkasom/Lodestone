package team.lodestar.lodestone.helpers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class CurioHelper {
    private static Provider provider = new Provider() {
        @Override
        public List<EquippedCurio> equipped(LivingEntity entity) {
            return List.of();
        }

        @Override
        public Optional<ImmutableTriple<String, Integer, ItemStack>> cosmetic(Predicate<ItemStack> filter, LivingEntity entity) {
            return Optional.empty();
        }
    };

    private CurioHelper() {
    }

    public static void install(Provider provider) {
        CurioHelper.provider = Objects.requireNonNull(provider);
    }

    public static Optional<EquippedCurio> getEquippedCurio(LivingEntity entity, Predicate<ItemStack> predicate) {
        return provider.equipped(entity).stream().filter(curio -> predicate.test(curio.stack())).findFirst();
    }

    public static Optional<EquippedCurio> getEquippedCurio(LivingEntity entity, Item curio) {
        return getEquippedCurio(entity, stack -> stack.is(curio));
    }

    public static boolean hasCurioEquipped(LivingEntity entity, Item curio) {
        return getEquippedCurio(entity, curio).isPresent();
    }

    public static ArrayList<ItemStack> getEquippedCurios(LivingEntity entity) {
        return getEquippedCurios(entity, stack -> true);
    }

    public static ArrayList<ItemStack> getEquippedCurios(LivingEntity entity, Predicate<ItemStack> predicate) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (EquippedCurio curio : provider.equipped(entity)) {
            if (predicate.test(curio.stack())) {
                stacks.add(curio.stack());
            }
        }
        return stacks;
    }

    public static Optional<ImmutableTriple<String, Integer, ItemStack>> findCosmeticCurio(Predicate<ItemStack> filter, LivingEntity entity) {
        return provider.cosmetic(filter, entity);
    }

    public record EquippedCurio(String identifier, int index, ItemStack stack) {
    }

    public interface Provider {
        List<EquippedCurio> equipped(LivingEntity entity);

        Optional<ImmutableTriple<String, Integer, ItemStack>> cosmetic(Predicate<ItemStack> filter, LivingEntity entity);
    }
}
