package fr.sunderia.sunderiautils.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


import java.util.Objects;
import java.util.Optional;

public abstract class PluginCommand extends BukkitCommand {
    private final CommandInfo info;
    protected final JavaPlugin plugin;

    /**
     * This constructor is used to register the command and check if the command has the correct annotation.
     * @param plugin An instance of the plugin.
     */
    protected PluginCommand(JavaPlugin plugin) {
        super("");
        this.plugin = plugin;
        info = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(info, "CommandInfo annotation is missing");
        setName(info.name());
        setAliases(ImmutableList.copyOf(info.aliases()));
        setDescription(info.description());
        setUsage(info.usage());
        setPermission(info.permission());
        setPermissionMessage(info.permissionMessage());
    }

    /**
     * @return The annotation of this command.
     */
    public CommandInfo getInfo() {
        return info;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!info.permission().isEmpty() && !sender.hasPermission(info.permission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        if(info.requiresPlayer()) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            } else {
                onCommand(player, args);
            }
            return true;
        }
        onCommand(sender, args);
        return true;
    }

    /**
     * @param args An array of arguments passed by the command sender
     * @param index The index of the argument to get.
     * @return The argument at the given index, or an empty optional if the index is out of bounds.
     */
    protected Optional<String> getArg(String[] args, int index) {
        if(args.length > index) {
            return Optional.of(args[index]);
        }
        return Optional.empty();
    }

    /**
     * This method is called when the command is executed by a player.
     * @param player The player who executed the command.
     * @param args The arguments passed by the player.
     */
    public void onCommand(Player player, String[] args) {}

    /**
     * This method is called when the command is executed by a non-player.
     * @param sender The sender who executed the command.
     * @param args The arguments passed by the sender.
     */
    public void onCommand(CommandSender sender, String[] args) {}
}