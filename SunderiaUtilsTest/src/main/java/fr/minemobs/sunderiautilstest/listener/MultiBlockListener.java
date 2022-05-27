package fr.minemobs.sunderiautilstest.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class MultiBlockListener implements Listener {

    @EventHandler
    public void onPlayerClickStructure(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;
        if(event.getClickedBlock().getType() != Material.EMERALD_BLOCK) return;
        Block center = event.getClickedBlock();
        if(!checkStructure(center)) return;
        event.getPlayer().sendMessage("You activated a structure !");
    }

    public boolean checkStructure(Block center) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 1; y++) {
                for (int z = -1; z < 2; z++) {
                    if(center.getRelative(x, y, z).getType() != Material.DIAMOND_BLOCK && (x != 0 || y != 0 || z != 0)) return false;
                }
            }
        }
        return true;
    }
}
