package fr.sunderia.sunderiautils.listeners;

import fr.sunderia.sunderiautils.events.ArmorEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerListener implements Listener {

    //todo: Test if it works

    private final Map<UUID, ItemStack[][]> playerArmor = new HashMap<>();
    private final Map<UUID, BukkitRunnable> playerRunnable = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerArmor.put(event.getPlayer().getUniqueId(), new ItemStack[][]{
                event.getPlayer().getInventory().getArmorContents(),
                null
        });
        playerRunnable.put(event.getPlayer().getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if(!playerArmor.containsKey(event.getPlayer().getUniqueId())) return;
                if(playerArmor.get(event.getPlayer().getUniqueId())[0] == null || playerArmor.get(event.getPlayer().getUniqueId())[1] == null) return;
                if(Objects.equals(playerArmor.get(event.getPlayer().getUniqueId())[0], playerArmor.get(event.getPlayer().getUniqueId())[1])) {
                    Bukkit.getPluginManager().callEvent(new ArmorEvent(event.getPlayer(), playerArmor.get(event.getPlayer().getUniqueId())));
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerArmor.remove(event.getPlayer().getUniqueId());
        playerRunnable.get(event.getPlayer().getUniqueId()).cancel();
        playerRunnable.remove(event.getPlayer().getUniqueId());
    }
}