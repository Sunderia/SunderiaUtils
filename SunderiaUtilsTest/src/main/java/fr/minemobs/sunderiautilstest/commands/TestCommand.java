package fr.minemobs.sunderiautilstest.commands;

import fr.sunderia.sunderiautils.commands.CommandInfo;
import fr.sunderia.sunderiautils.commands.PluginCommand;
import fr.sunderia.sunderiautils.commands.SubCommand;
import fr.sunderia.sunderiautils.utils.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@CommandInfo(name = "test")
public class TestCommand extends PluginCommand {

    /**
     * This constructor is used to register the command and check if the command has the correct annotation.
     *
     * @param plugin An instance of the plugin.
     */
    public TestCommand(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * This method is called when the command is run with the test argument.
     * You can name your method as you want, but it must have a {@link Player} or a {@link org.bukkit.command.CommandSender} as first argument and a {@code String[]} as second argument.
     * @param player The player who executed the command.
     * @param args The arguments of the command.
     */
    @SubCommand(name = "test")
    public void test(Player player, String[] args) {
        new InventoryBuilder("Something", new InventoryBuilder.Shape(
                """
                AAAABAAAA
                A   B   A
                BBBBBBBBB
                A   B   A
                AAAABAAAA
                """, Map.of('A', new ItemStack(Material.DIAMOND), 'B', new ItemStack(Material.EMERALD))))
                .onClick((e, gui) -> {
                    e.setCancelled(true);
                    int slot = e.getSlot();
                    ItemStack is = e.getCurrentItem();
                    if(ItemStackUtils.isNotAirNorNull(is)) {
                        ReflectionUtils.renameCurrentInv(player, "You clicked on " + StringUtils.capitalizeWord(is.getType().name()));
                        if(is.getType() == Material.DIAMOND) {
                            gui.setItem(new ItemStack(Material.EMERALD), slot);
                        } else {
                            gui.setItem(new ItemStack(Material.DIAMOND), slot);
                        }
                    }
                })
                .build().openInventory(player);
    }

}
