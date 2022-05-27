package fr.sunderia.sunderiautils.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AnvilCrushRecipe implements Recipe {

    private static final Map<NamespacedKey, AnvilCrushRecipe> recipes = new HashMap<>();

    private final NamespacedKey key;
    private final Material base;
    private final ItemStack result;

    public AnvilCrushRecipe(NamespacedKey key, Material base, ItemStack result) {
        this.key = key;
        this.base = base;
        this.result = result;
        recipes.put(key, this);
    }

    public static ImmutableList<AnvilCrushRecipe> getRecipesAsList() {
        return ImmutableList.copyOf(recipes.values());
    }

    /**
     * Return the recipe from the given {@link NamespacedKey key}.
     * @param key The key to get the recipe from.
     * @return The recipe from the given key or null if no recipe was found.
     */
    @Nullable
    public static AnvilCrushRecipe getRecipeFromKey(NamespacedKey key) {
        if(recipes.containsKey(key)) return null;
        return recipes.get(key);
    }

    public static ImmutableMap<NamespacedKey, AnvilCrushRecipe> getRecipes() {
        return ImmutableMap.copyOf(recipes);
    }

    public NamespacedKey getKey() {
        return key;
    }

    public Material getBase() {
        return base;
    }

    @NotNull
    @Override
    public ItemStack getResult() {
        return result;
    }
}
