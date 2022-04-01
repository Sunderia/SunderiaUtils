package fr.minemobs.sunderiautilstest;

import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.recipes.AnvilCrushRecipe;
import fr.sunderia.sunderiautils.recipes.AnvilRecipe;
import fr.sunderia.sunderiautils.recipes.WaterRecipe;
import fr.sunderia.sunderiautils.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        SunderiaUtils.of(this);
        try {
            SunderiaUtils.registerCommands(this.getClass().getPackageName() + ".commands");
            SunderiaUtils.registerListeners(this.getClass().getPackageName() + ".listener");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AnvilCrushRecipe crushRecipe =
                new AnvilCrushRecipe(new NamespacedKey(this, "amethyst_crush"), Material.AMETHYST_BLOCK,
                        new ItemBuilder(Material.AMETHYST_SHARD).setDisplayName("§bWeird Amethyst").build());
        AnvilRecipe anvilRecipe =
                new AnvilRecipe(Material.EXPERIENCE_BOTTLE, Material.FEATHER, new ItemBuilder(Material.ALLIUM).setDisplayName("Weird thing").build());
        WaterRecipe waterRecipe = new WaterRecipe(new NamespacedKey(this, "water_recipe"),
                Material.COAL, Material.IRON_INGOT, new ItemBuilder(Material.DIAMOND).setDisplayName("Botania").build());
    }

}
