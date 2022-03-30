package fr.sunderia.sunderiautils.listeners;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public class WorldListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) throws IOException {
        if(SunderiaUtils.getRandom().nextInt(100) > 5) return;
        StructureManager manager = Bukkit.getStructureManager();
        Location loc = event.getWorld().getHighestBlockAt(SunderiaUtils.getRandom().nextInt(16), SunderiaUtils.getRandom().nextInt(16)).getLocation();
        //System.out.println(loc);
        //manager.loadStructure(getClass().getResource("/stone_tall_tower.nbt").openStream()).fill(loc, new BlockVector(7, 30, 7), false);
    }

}
