package fr.sunderia.sunderiautils.utils;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import fr.sunderia.sunderiautils.listeners.InventoryListener;
import fr.sunderia.sunderiautils.utils.InventoryBuilder.InventoryListeners;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Beta
//TODO: Make this mutable
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

    public String getName() {
        return name;
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }

    public int getSize() {
        return size;
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, name);
        inventory.setContents(itemStacks);
        player.openInventory(inventory);
        InventoryListener.addListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gui gui = (Gui) o;
        return size == gui.size && Objects.equal(name, gui.name) && Objects.equal(itemStacks, gui.itemStacks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, size, itemStacks);
    }
}