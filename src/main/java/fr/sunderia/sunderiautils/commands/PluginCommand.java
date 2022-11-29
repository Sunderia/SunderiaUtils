package fr.sunderia.sunderiautils.commands;

import com.google.common.collect.ImmutableList;
import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
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

    PluginCommand(JavaPlugin plugin, String name, String[] aliases, String description, String usage, String permission, String permissionMessage) {
        super("");
        this.plugin = plugin;
        setName(name);
        setAliases(Arrays.asList(aliases));
        setDescription(description);
        setUsage(usage);
        setPermission(permission);
        setPermissionMessage(permissionMessage);
        this.info = null;
    }

    /**
     * @return The annotation of this command. It may be null if the command has been built using the {@link CommandBuilder}
     */
    @Nullable
    public CommandInfo getInfo() {
        return info;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return true;
        }
        if(info == null || info.requiresPlayer()) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            } else {
                onCommand(player, args);
                if(args.length != 0) callSubCommands(player, args);
            }
            return true;
        }
        onCommand(sender, args);
        if(args.length != 0) callSubCommands(sender, args);
        return true;
    }

    private void callSubCommands(CommandSender sender, String[] args) {
        for (Method method : getClass().getDeclaredMethods()) {
            if(method.getParameterCount() != 2 || !CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) || !String[].class.isAssignableFrom(method.getParameterTypes()[1])) continue;
            if(!method.getParameterTypes()[0].isAssignableFrom(sender.getClass())) continue;
            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if(subCommand == null) continue;
            String name = subCommand.name();
            for(int i = 0; i < args.length; i++) {
                if(args[i].equals(name) && subCommand.position() == i) {
                    int nbrOfArgs = subCommand.numberOfArguments() + i + 1;
                    if(args.length < nbrOfArgs) nbrOfArgs = args.length;
                    String[] subArgs = Arrays.copyOfRange(args, i + 1, nbrOfArgs);
                    try {
                        method.invoke(this, method.getParameterTypes()[0].cast(sender), subArgs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param args An array of arguments passed by the command sender
     * @param index The index of the argument to get.
     * @return The argument at the given index, or an empty optional if the index is out of bounds.
     */
    protected boolean argIsEquals(String[] args, int index, String value) {
        Optional<String> arg = getArg(args, index);
        return arg.isPresent() && arg.get().equalsIgnoreCase(value);
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
