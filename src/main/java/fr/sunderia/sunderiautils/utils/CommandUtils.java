package fr.sunderia.sunderiautils.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandUtils {

    private CommandUtils() {}

    public static Optional<String> get(String[] args, int i) {
        try {
            return Optional.ofNullable(args[i]);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Defaults to the original player
     */
    public static Player getTarget(Player player, String[] args, int i) {
        var name = get(args, i);
        return name.isPresent() ? Bukkit.getPlayer(name.get()) : player;
    }
}