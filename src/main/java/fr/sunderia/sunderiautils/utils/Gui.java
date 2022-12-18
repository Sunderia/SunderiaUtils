package fr.sunderia.sunderiautils.utils;

import com.google.common.collect.ImmutableList;
import fr.sunderia.sunderiautils.listeners.InventoryListener;
import fr.sunderia.sunderiautils.utils.InventoryBuilder.InventoryListeners;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Gui {

    private final Inventory inventory;
    private final InventoryListeners listener;
    private final UUID uuid;

    public Gui(String name, int rows, List<ItemStack> itemStacks, InventoryListeners listener) {
        int size = rows * 9;
        Inventory inventory = Bukkit.createInventory(null, size, name);
        inventory.setContents(itemStacks.subList(0, size).toArray(ItemStack[]::new));
        this.inventory = inventory;
        this.listener = listener;
        this.uuid = UUID.randomUUID();
    }

    public InventoryListeners getListener() {
        return listener;
    }

    public ImmutableList<ItemStack> getItemStacks() {
        return ImmutableList.copyOf(inventory.getContents());
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setItem(ItemStack stack, int i) {
        if(i < 0 || i >= inventory.getSize()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        inventory.setItem(i, stack);
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... stacks) {
        return inventory.addItem(stacks);
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
        InventoryListener.addListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gui gui = (Gui) o;
        return gui.uuid.equals(uuid) && gui.getSize() == getSize() && Arrays.equals(inventory.getContents(), gui.inventory.getContents());
    }

    @Override
    public int hashCode() {
        return inventory.hashCode();
    }
}