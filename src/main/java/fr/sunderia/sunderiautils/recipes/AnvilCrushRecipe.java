package fr.sunderia.sunderiautils.recipes;

import com.google.common.collect.ImmutableList;
import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AnvilCrushRecipe {

    private static final Map<String, AnvilCrushRecipe> recipes = new HashMap<>();

    private final Material base;
    private final ItemStack result;

    public AnvilCrushRecipe(Material base, ItemStack result) {
        this.base = base;
        this.result = result;
        recipes.put(SunderiaUtils.getPlugin().getName(), this);
    }

    public static ImmutableList<AnvilCrushRecipe> getRecipes() {
        return ImmutableList.copyOf(recipes.values());
    }

    public Material getBase() {
        return base;
    }

    public ItemStack getResult() {
        return result;
    }
}
