package fr.minemobs.sunderiautilstest.listener;

import fr.minemobs.sunderiautilstest.PacketSender;
import fr.minemobs.sunderiautilstest.TestPlugin;
import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.customblock.CustomBlock;
import fr.sunderia.sunderiautils.enchantments.CustomEnchantment;
import fr.sunderia.sunderiautils.utils.InventoryBuilder;
import fr.sunderia.sunderiautils.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    private final ItemStack builder = new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayName("feur")
            .onInteract(e -> e.getPlayer().sendMessage("You have clicked on the item"))
            .build();
    private final CustomEnchantment ce = new CustomEnchantment.CustomEnchantmentBuilder("test").onInteract(e -> e.getPlayer().sendMessage("Hello World!")).onBreak(e -> {
        if(TestPlugin.getModdedPlayers().contains(e.getPlayer().getUniqueId())) {
            PacketSender.sendEnchantedBreakPacket(e.getPlayer());
        } else {
            e.getPlayer().sendMessage("You have broken a block with the test enchantment");
        }
    }).addOnItem(builder);
    private final List<CustomEnchantment> enchantments = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enchantments.contains(ce)) {
            enchantments.add(ce);
        }
        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setItem(4, builder.clone());
        event.getPlayer().getInventory().setItem(2, new CustomBlock.Builder(SunderiaUtils.key("ruby_ore"), 1)
                .setDrops(new ItemBuilder(Material.EMERALD).setDisplayName(ChatColor.RED + "RUBY").build()).build().getAsItem());
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if(event.getMessage().equalsIgnoreCase("/test")) {
            event.getPlayer().openInventory(new InventoryBuilder("Something", new InventoryBuilder.Shape(
                    """
                    AAAABAAAA
                    A   B   A
                    BBBBBBBBB
                    A   B   A
                    AAAABAAAA
                    """, Map.of('A', new ItemStack(Material.DIAMOND), 'B', new ItemStack(Material.EMERALD)))).build());
        }
    }
}
