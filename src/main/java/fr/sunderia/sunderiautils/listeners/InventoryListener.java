package fr.sunderia.sunderiautils.listeners;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static fr.sunderia.sunderiautils.utils.InventoryBuilder.InventoryListeners;
import static java.util.Objects.nonNull;

public final class InventoryListener implements Listener {

    private static final List<InventoryListeners> LISTENERS = new ArrayList<>();

    public static void addListener(InventoryListeners listener) {
        LISTENERS.add(listener);
    }

    public static void removeListener(InventoryListeners listener) {
        listener.askedForRemoval = true;
    }

    private static Stream<InventoryListeners> getListeners() {
        return LISTENERS.stream().filter(Objects::nonNull).filter(listener -> !listener.closed);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        LISTENERS.forEach(listener -> {
            listener.closed = false;
            listener.getOpenEventConsumer().accept(event, listener.getInventory());
            if(nonNull(listener.getUpdateEventConsumer())) {
                listener.setTask(Bukkit.getScheduler().runTaskTimer(SunderiaUtils.getPlugin(),
                        () -> listener.getUpdateEventConsumer().accept(event, listener.getInventory()),
                        listener.getRunnableDelay(), listener.getRunnableTime()));
            }
        });
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        for (int i = 0; i < LISTENERS.size(); i++) {
            InventoryListeners listener = LISTENERS.get(i);
            listener.closed = true;
            listener.getCloseEventConsumer().accept(event, listener.getInventory());
            if(listener.askedForRemoval) {
                LISTENERS.remove(i);
                i--;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        getListeners().forEach(listener -> listener.getClickEventConsumer().accept(event, listener.getInventory()));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        getListeners().forEach(listener -> listener.getDragEventConsumer().accept(event, listener.getInventory()));
    }
}
