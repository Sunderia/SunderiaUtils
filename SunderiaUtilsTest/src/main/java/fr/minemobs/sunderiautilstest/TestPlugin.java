package fr.minemobs.sunderiautilstest;

import com.google.common.collect.ImmutableList;
import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.recipes.AnvilCrushRecipe;
import fr.sunderia.sunderiautils.recipes.AnvilRecipe;
import fr.sunderia.sunderiautils.recipes.WaterRecipe;
import fr.sunderia.sunderiautils.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestPlugin extends JavaPlugin implements PluginMessageListener {

    private static final List<UUID> MODDED_PLAYERS = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        SunderiaUtils.of(this);
        try {
            SunderiaUtils.registerCommands(this.getClass().getPackageName() + ".commands");
            SunderiaUtils.registerListeners(this.getClass().getPackageName() + ".listener");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new AnvilCrushRecipe(new NamespacedKey(this, "amethyst_crush"), Material.AMETHYST_BLOCK,
                new ItemBuilder(Material.AMETHYST_SHARD).setDisplayName("§bWeird Amethyst").build());
        new AnvilRecipe(Material.EXPERIENCE_BOTTLE, Material.FEATHER, new ItemBuilder(Material.ALLIUM).setDisplayName("Weird thing").build());
        new WaterRecipe(new NamespacedKey(this, "water_recipe"),
                Material.COAL, Material.IRON_INGOT, new ItemBuilder(Material.DIAMOND).setDisplayName("Botania").build());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "sunderiaclient:packet", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "sunderiaclient:packet");
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        System.out.println("Received message from channel " + channel);
        String subchannel = new String(message);
        subchannel = subchannel.substring(1);
        System.out.println("Subchannel: " + subchannel);
        if(subchannel.equals("join")) {
            System.out.println(player.getName() + " is using SunderiaClient.");
            MODDED_PLAYERS.add(player.getUniqueId());
        }
    }

    public static ImmutableList<UUID> getModdedPlayers() {
        return ImmutableList.copyOf(MODDED_PLAYERS);
    }
}
