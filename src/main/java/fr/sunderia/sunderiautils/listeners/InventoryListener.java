package fr.sunderia.sunderiautils.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.ArrayList;
import java.util.List;

import static fr.sunderia.sunderiautils.utils.InventoryBuilder.InventoryListeners;

public final class InventoryListener implements Listener {

    //TODO: I don't think that it would work but I'll work on it later
    private static final List<InventoryListeners> LISTENERS = new ArrayList<>();

    public static void addListener(InventoryListeners listener) {
        LISTENERS.add(listener);
    }

    public static void removeListener(InventoryListeners listener) {
        LISTENERS.remove(listener);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        LISTENERS.forEach(listener -> listener.getOpenEventConsumer().accept(event));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        LISTENERS.forEach(listener -> listener.getCloseEventConsumer().accept(event));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        LISTENERS.forEach(listener -> listener.getClickEventConsumer().accept(event));
    }

    public void tick(InventoryEvent inventory) {
        LISTENERS.forEach(listener -> listener.getUpdateEventConsumer().accept(inventory));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        LISTENERS.forEach(listener -> listener.getDragEventConsumer().accept(event));
    }
}
