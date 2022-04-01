package fr.sunderia.sunderiautils.listeners;

import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.recipes.WaterRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (!WaterRecipe.isBase(event.getItemDrop().getItemStack())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Location baseLoc = event.getItemDrop().getLocation();
                if(baseLoc.getWorld().getBlockAt(baseLoc).getType() != Material.WATER) {
                    return;
                }
                event.getItemDrop().getWorld().getNearbyEntities(event.getItemDrop().getLocation(), 0.5, 0.15, 0.5,
                        e -> e instanceof Item item && !item.equals(event.getItemDrop()) && item.getItemStack().getType() != Material.AIR).forEach(entity -> {
                    Item item = (Item) entity;
                    if(event.getItemDrop().getItemStack().getType() == Material.AIR) return;
                    Optional<WaterRecipe> recipeFromItems = WaterRecipe.getRecipeFromItems(event.getItemDrop().getItemStack(), item.getItemStack());
                    if (recipeFromItems.isEmpty()) return;
                    int amount = Math.min(event.getItemDrop().getItemStack().getAmount(), item.getItemStack().getAmount());
                    for (int i = 0; i < amount; i++) {
                        baseLoc.getWorld().dropItemNaturally(baseLoc, recipeFromItems.get().getResult());
                        reduceAmount(event.getItemDrop());
                        reduceAmount(item);
                    }
                });
                this.cancel();
            }
        }.runTaskTimer(SunderiaUtils.getPlugin(), 0, 20);
    }

    private void reduceAmount(Item item) {
        if(item.getItemStack().getAmount() == 1) {
            item.remove();
        } else {
            item.getItemStack().setAmount(item.getItemStack().getAmount() - 1);
        }
    }

}
