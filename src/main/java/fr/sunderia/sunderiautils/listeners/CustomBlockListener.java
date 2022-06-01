package fr.sunderia.sunderiautils.listeners;

import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.customblock.CustomBlock;
import fr.sunderia.sunderiautils.event.CustomBlockBreakEvent;
import fr.sunderia.sunderiautils.event.CustomBlockPlaceEvent;
import fr.sunderia.sunderiautils.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomBlockListener implements Listener {

    private static final Map<NamespacedKey, CustomBlock> blocks = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getType() != Material.SPAWNER || event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        if(!spawner.getPersistentDataContainer().has(CustomBlock.CMD_KEY, PersistentDataType.INTEGER)) {
            return;
        }
        int cmd = spawner.getPersistentDataContainer().get(CustomBlock.CMD_KEY, PersistentDataType.INTEGER);
        Optional<CustomBlock> cb = CustomBlockListener.blocks.values().stream()
                .filter(customBlock -> customBlock.getCustomModelData() == cmd && customBlock.getLoc().equals(event.getBlock().getLocation())).findFirst();
        if (cb.isEmpty()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new CustomBlockBreakEvent(event.getBlock(), event.getPlayer(), cb.get()));
            }
        }.runTaskLater(SunderiaUtils.getPlugin(), 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCustomBlockPlaced(BlockPlaceEvent event) {
        if(event.getBlock().getType() != Material.LIME_GLAZED_TERRACOTTA || !event.getItemInHand().hasItemMeta() || !event.getItemInHand().getItemMeta().hasDisplayName() ||
                !event.getItemInHand().getItemMeta().hasCustomModelData()) return;
        Optional<CustomBlock> opBlock = CustomBlock.getRegisteredBlocks().stream()
                .filter(block -> ItemStackUtils.isSameItem(event.getItemInHand(), block.getAsItem()) &&
                        event.getItemInHand().getItemMeta().hasCustomModelData() &&
                        block.getAsItem().getItemMeta().getCustomModelData() == event.getItemInHand().getItemMeta().getCustomModelData()).findFirst();
        if(opBlock.isEmpty()) {
            System.out.println("Empty lol");
            return;
        }
        CustomBlock block = opBlock.get();
        block.setLoc(event.getBlock().getLocation());
        event.setCancelled(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setBlock(block.getLoc());
                blocks.put(block.getName(), block);
                CustomBlockPlaceEvent e = new CustomBlockPlaceEvent(event.getBlockPlaced(), event.getBlockReplacedState(),
                        event.getBlockAgainst(), event.getItemInHand(), event.getPlayer(), event.canBuild(),
                        event.getHand(), block);
                if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
                Bukkit.getPluginManager().callEvent(e);
            }
        }.runTaskLater(SunderiaUtils.getPlugin(), 2);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCustomBlockBreak(CustomBlockBreakEvent event) {
        for(ItemStack stack : event.getCustomBlock().getDrop()){
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
        }
        event.setExpToDrop(event.getCustomBlock().getXp());
        blocks.remove(event.getCustomBlock().getName());
    }

    public static void putCustomBlock(CustomBlock cb) {
        blocks.put(cb.getName(), cb);
    }
}