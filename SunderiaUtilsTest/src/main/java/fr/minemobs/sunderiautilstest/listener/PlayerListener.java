package fr.minemobs.sunderiautilstest.listener;

import fr.sunderia.sunderiautils.enchantments.CustomEnchantment;
import fr.sunderia.sunderiautils.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    private final ItemStack builder = new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayName("feur")
            .onInteract(e -> e.getPlayer().sendMessage("You have clicked on the item"))
            .build();
    private final CustomEnchantment ce = new CustomEnchantment.CustomEnchantmentBuilder("test").onInteract(e -> e.getPlayer().sendMessage("Hello World!")).onBreak(e -> e.getPlayer().sendMessage("You have broken a block with the test enchantment")).addOnItem(builder);
    private final List<CustomEnchantment> enchantments = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enchantments.contains(ce)) {
            enchantments.add(ce);
        }
        event.getPlayer().getInventory().addItem(builder.clone());
    }
}
