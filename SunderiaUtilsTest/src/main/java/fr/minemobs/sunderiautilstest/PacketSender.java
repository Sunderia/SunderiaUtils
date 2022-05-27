package fr.minemobs.sunderiautilstest;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PacketSender {

    private static final JavaPlugin plugin;

    static {
        plugin = JavaPlugin.getPlugin(TestPlugin.class);
    }

    public static boolean isUsingClient(Player player) {
        return TestPlugin.getModdedPlayers().contains(player.getUniqueId());
    }

    public static void sendEnchantedBreakPacket(Player player) {
        if (isUsingClient(player)) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("EnchantedBreak");
            player.sendPluginMessage(plugin, "sunderiaclient:packet", out.toByteArray());
        }
    }

}
