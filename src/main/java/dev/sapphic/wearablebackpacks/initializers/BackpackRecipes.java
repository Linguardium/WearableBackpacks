package dev.sapphic.wearablebackpacks.initializers;

import dev.sapphic.wearablebackpacks.recipe.BackpackDyeingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BackpackRecipes {
    public static final RecipeSerializer<BackpackDyeingRecipe> BACKPACK_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, BackpackDyeingRecipe.ID, BackpackDyeingRecipe.SERIALIZER);
    public static void init() { }

}
