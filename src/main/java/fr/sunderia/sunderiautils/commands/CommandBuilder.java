package fr.sunderia.sunderiautils.commands;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class CommandBuilder {

    private final String name;
    private String permission = "", permissionMessage = "You don't have the permission to use this command", usage = "/<command>", description = "No description";
    private String[] aliases = new String[0];
    private BiConsumer<Player, String[]> function;

    public CommandBuilder(String name) {
        this.name = name;
    }

    public CommandBuilder setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public CommandBuilder setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
        return this;
    }

    public CommandBuilder setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder setAliases(String[] aliases) {
        this.aliases = aliases;
        return this;
    }

    public CommandBuilder setFunction(BiConsumer<Player, String[]> function) {
        this.function = function;
        return this;
    }

    public void build() {
        PluginCommand command = new PluginCommand(SunderiaUtils.getPlugin(), name, aliases, description, usage, permission, permissionMessage) {
            @Override
            public void onCommand(Player player, String[] args) {
                function.accept(player, args);
            }

            @Override
            public void onCommand(CommandSender sender, String[] args) {
            }
        };
        SunderiaUtils.registerCommand(command);
    }
}
