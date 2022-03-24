package fr.sunderia.sunderiautils.listeners;

import fr.sunderia.sunderiautils.recipes.AnvilCrushRecipe;
import fr.sunderia.sunderiautils.recipes.AnvilRecipe;
import fr.sunderia.sunderiautils.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

import java.util.Optional;

public class RecipeListener implements Listener {

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        if(ItemStackUtils.isAirOrNull(inv.getItem(0)) || ItemStackUtils.isAirOrNull(inv.getItem(1))) return;
        Optional<AnvilRecipe> recipe = AnvilRecipe.getRecipes().stream().filter(anvilRecipe -> anvilRecipe != null && anvilRecipe.isEquals(inv)).findFirst();
        if(recipe.isEmpty()) return;
        event.setResult(recipe.get().getResult());
    }

    @EventHandler
    public void onAnvilClickedResult(InventoryClickEvent event) {
        if(event.getInventory().getType() != InventoryType.ANVIL || event.getRawSlot() != 2) return;
        if(event.getWhoClicked().getInventory().firstEmpty() == -1) return;
        AnvilInventory inv = (AnvilInventory) event.getInventory();
        if (AnvilRecipe.getRecipes().stream().noneMatch(recipe -> recipe != null && recipe.isEquals(inv))) return;
        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        inv.getItem(0).setAmount(inv.getItem(0).getAmount() - 1);
        inv.getItem(1).setAmount(inv.getItem(1).getAmount() - 1);
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if(!(event.getEntity() instanceof FallingBlock block) || !block.getBlockData().getMaterial().toString().endsWith("ANVIL")) return;
        Block b = block.getWorld().getBlockAt(block.getLocation().clone().subtract(0, 1, 0));
        if(b.isEmpty() || b.isLiquid() || b.isPassable()) return;
        AnvilCrushRecipe.getRecipesAsList().stream().filter(recipe -> recipe != null && recipe.getBase() == b.getType()).findFirst().ifPresent(recipe -> {
            b.setType(Material.AIR);
            block.getWorld().dropItemNaturally(b.getLocation(), recipe.getResult());
        });
    }
}