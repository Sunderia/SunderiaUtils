package fr.sunderia.sunderiautils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class ArmorEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final ItemStack[] oldPlayerArmor;
    private final ItemStack[] newPlayerArmor;

    public ArmorEvent(@NotNull Player player, ItemStack[][] armor) {
        super(player);
        this.oldPlayerArmor = armor[0];
        this.newPlayerArmor = armor[1];
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ItemStack[] getOldPlayerArmor() {
        return oldPlayerArmor;
    }

    public ItemStack[] getNewPlayerArmor() {
        return newPlayerArmor;
    }
}
