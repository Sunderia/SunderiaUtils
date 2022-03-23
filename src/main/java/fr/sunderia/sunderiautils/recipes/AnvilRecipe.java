package fr.sunderia.sunderiautils.recipes;

import com.google.common.collect.ImmutableList;
import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AnvilRecipe {

    private static final Map<String, AnvilRecipe> recipes = new HashMap<>();

    private final ItemStack base;
    private final ItemStack addition;
    private final ItemStack result;

    public AnvilRecipe(ItemStack base, ItemStack addition, ItemStack result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
        recipes.put(SunderiaUtils.getPlugin().getName(), this);
    }

    public AnvilRecipe(Material base, ItemStack addition, ItemStack result) {
        this(new ItemStack(base), addition, result);
    }

    public AnvilRecipe(ItemStack base, Material addition, ItemStack result) {
        this(base, new ItemStack(addition), result);
    }

    public AnvilRecipe(Material base, Material addition, ItemStack result) {
        this(new ItemStack(base), new ItemStack(addition), result);
    }

    public static ImmutableList<AnvilRecipe> getRecipes() {
        return ImmutableList.copyOf(recipes.values());
    }

    public ItemStack getBase() {
        return base;
    }

    public ItemStack getAddition() {
        return addition;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean isEquals(AnvilInventory inv) {
        return ItemStackUtils.isSameItem(inv.getItem(0), getBase()) && ItemStackUtils.isSameItem(inv.getItem(1), getAddition());
    }
}
