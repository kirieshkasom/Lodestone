package team.lodestar.lodestone.modules.toolkit.recipe;

import net.minecraft.resources.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * A Basic implementation for a custom recipe type along with some static helper functions for fetching recipes.
 */
public class LodestoneRecipeType<T extends Recipe<?>> implements RecipeType<T> {

    public final ResourceLocation id;

    public LodestoneRecipeType(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
