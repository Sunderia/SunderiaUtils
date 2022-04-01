package fr.sunderia.sunderiautils.recipes;

import fr.sunderia.sunderiautils.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WaterRecipe {

    private static final Map<NamespacedKey, WaterRecipe> recipes = new HashMap<>();
    private final NamespacedKey key;
    private final ItemStack base;
    private final ItemStack addition;
    private final ItemStack result;

    public WaterRecipe(NamespacedKey key, ItemStack base, ItemStack addition, ItemStack result) {
        this.key = key;
        this.base = base;
        this.addition = addition;
        this.result = result;
        recipes.put(key, this);
    }

    public WaterRecipe(NamespacedKey key, Material base, ItemStack addition, ItemStack result) {
        this(key, new ItemStack(base), addition, result);
    }

    public WaterRecipe(NamespacedKey key, ItemStack base, Material addition, ItemStack result) {
        this(key, base, new ItemStack(addition), result);
    }

    public WaterRecipe(NamespacedKey key, Material base, Material addition, ItemStack result) {
        this(key, new ItemStack(base), new ItemStack(addition), result);
    }

    public static WaterRecipe[] getAllRecipes() {
        return recipes.values().toArray(new WaterRecipe[0]);
    }

    public static WaterRecipe[] getAllRecipesFromPlugin(JavaPlugin plugin) {
        return recipes.values().stream().filter(r -> r.getKey().getNamespace().equals(plugin.getName().toLowerCase())).toArray(WaterRecipe[]::new);
    }

    public static WaterRecipe getRecipe(NamespacedKey key) {
        return recipes.get(key);
    }

    public static boolean isBase(ItemStack item) {
        return recipes.values().stream().anyMatch(i -> ItemStackUtils.isSimilar(i.getBase(), item) || ItemStackUtils.isSameItem(i.getBase(), item));
    }

    public static boolean isAddition(ItemStack item) {
        return recipes.values().stream().anyMatch(i -> ItemStackUtils.isSimilar(i.getAddition(), item) || ItemStackUtils.isSameItem(i.getAddition(), item));
    }

    public static Optional<WaterRecipe> getRecipeFromItems(ItemStack base, ItemStack addition) {
        return recipes.values().stream().filter(r -> ItemStackUtils.isSimilar(r.getBase(), base) && ItemStackUtils.isSimilar(r.getAddition(), addition)).findFirst();
    }

    public NamespacedKey getKey() {
        return key;
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
}
