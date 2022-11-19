package fr.sunderia.sunderiautils.utils;

import com.google.common.annotations.Beta;
import fr.sunderia.sunderiautils.utils.InventoryBuilder.InventoryListeners;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Beta
public class Gui {

    private final String name;
    private final int size;
    private final ItemStack[] itemStacks;
    private final InventoryListeners listener;

    public Gui(String name, int rows, List<ItemStack> itemStacks, InventoryListeners listener) {
        this.name = name;
        this.size = rows * 9;
        this.itemStacks = itemStacks.toArray(ItemStack[]::new);
        this.listener = listener;
    }

    public InventoryListeners getListener() {
        return listener;
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, name);
        inventory.setContents(itemStacks);
        player.openInventory(inventory);
    }
}