package fr.minemobs.sunderiautilstest.listener;

import fr.sunderia.sunderiautils.enchantments.EnchantmentBuilder;
import fr.sunderia.sunderiautils.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final ItemStack builder = new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("feur")
            .addEnchant(
                    new EnchantmentBuilder("test")
                            .onBreakBlock(e -> e.getPlayer().sendMessage("Your item has the Test enchantment"))
                            .build(), 1)
            .onInteract(e -> e.getPlayer().sendMessage("You have clicked on the item"))
            .build();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        event.getPlayer().getInventory().addItem(builder.clone());
    }
}
