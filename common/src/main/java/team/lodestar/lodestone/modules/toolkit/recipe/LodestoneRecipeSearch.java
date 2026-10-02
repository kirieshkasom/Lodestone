package team.lodestar.lodestone.modules.toolkit.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LodestoneRecipeSearch<T extends RecipeInput, K extends Recipe<T>> {

    protected final Level level;
    protected final RecipeType<K> recipeType;

    public static <T extends RecipeInput, K extends Recipe<T>> LodestoneRecipeSearch<T, K> search(Level level, DeferredHolder<RecipeType<?>, LodestoneRecipeType<K>> holder) {
        return search(level, holder.get());
    }
    public static <T extends RecipeInput, K extends Recipe<T>> LodestoneRecipeSearch<T, K> search(Level level, Supplier<RecipeType<K>> recipeType) {
        return search(level, recipeType.get());
    }
    public static <T extends RecipeInput, K extends Recipe<T>> LodestoneRecipeSearch<T, K> search(Level level, RecipeType<K> recipeType) {
        return new LodestoneRecipeSearch<>(level, recipeType);
    }

    protected LodestoneRecipeSearch(Level level, RecipeType<K> recipeType) {
        this.level = level;
        this.recipeType = recipeType;
    }

    public K findRecipe(ResourceLocation id) {
        return findRecipe(r -> {
            List<RecipeHolder<K>> recipeHolders = getRecipeHolders();
            for (RecipeHolder<K> recipeHolder : recipeHolders) {
                if (recipeHolder.id().equals(id)) {
                    return true;
                }
            }
            return false;
        });
    }

    public K findRecipe(T recipeInput) {
        return findRecipe(r -> r.matches(recipeInput, level));
    }

    public K findRecipe(Predicate<K> predicate) {
        var recipes = getRecipeHolders();
        for (RecipeHolder<K> recipe : recipes) {
            K value = recipe.value();
            if (predicate.test(value)) {
                return value;
            }
        }
        return null;
    }

    public List<K> findRecipes(T recipeInput) {
        return findRecipes(r -> r.matches(recipeInput, level));
    }

    public List<K> findRecipes(Predicate<K> predicate) {
        var result = new ArrayList<K>();
        var recipes = getRecipeHolders();
        for (RecipeHolder<K> recipe : recipes) {
            K value = recipe.value();
            if (predicate.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public List<K> getAllRecipes() {
        return getRecipeHolders().stream().map(RecipeHolder::value).collect(Collectors.toList());
    }

    public List<RecipeHolder<K>> getRecipeHolders() {
        return level.getRecipeManager().getAllRecipesFor(recipeType);
    }
}