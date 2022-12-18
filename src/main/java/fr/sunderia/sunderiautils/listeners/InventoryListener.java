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

public final class InventoryListener implements Listener {

    //TODO: I don't think that it would work, but I'll work on it later
    private static final List<InventoryListeners> LISTENERS = new ArrayList<>();

    public static void addListener(InventoryListeners listener) {
        LISTENERS.add(listener);
    }

    public static void removeListener(InventoryListeners listener) {
        LISTENERS.remove(listener);
    }

    private static Stream<InventoryListeners> getListeners() {
        return LISTENERS.stream().filter(listener -> listener != null && !listener.closed);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        LISTENERS.forEach(listener -> {
            listener.closed = false;
            listener.getOpenEventConsumer().accept(event);
            if(listener.getUpdateEventConsumer() != null) {
                listener.setTask(Bukkit.getScheduler().runTaskTimer(SunderiaUtils.getPlugin(), () -> listener.getUpdateEventConsumer().accept(event), listener.getRunnableDelay(), listener.getRunnableTime()));
            }
        });
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        LISTENERS.forEach(listener -> {
            listener.closed = true;
            listener.getCloseEventConsumer().accept(event);
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        getListeners().forEach(listener -> listener.getClickEventConsumer().accept(event));
    }

    public void tick(InventoryEvent inventory) {
        getListeners().map(InventoryListeners::getUpdateEventConsumer).filter(Objects::nonNull).forEach(consumer -> consumer.accept(inventory));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        getListeners().forEach(listener -> listener.getDragEventConsumer().accept(event));
    }
}
