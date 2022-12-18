package fr.sunderia.sunderiautils.utils;

import fr.sunderia.sunderiautils.SunderiaUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {

    private ReflectionUtils() {}

    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static ServerPlayer getNMSPlayer(Player player) {
        Class<?> clazz = getCraftBukkitClass("entity.CraftPlayer");
        Object cast = clazz.cast(player);
        try {
            return (ServerPlayer) clazz.getMethod("getHandle").invoke(cast);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            SunderiaUtils.LOGGER.error("Could not invoke getHandle", e);
            return null;
        }
    }

    public static void renameCurrentInv(Player player, String name) {
        MenuType<?> type = getNMSPlayer(player).containerMenu.getType();
        renameCurrentInv(player, name, type);
    }

    private static void renameCurrentInv(final Player player, final String title, final MenuType<?> containerType) {
        final ServerPlayer p = getNMSPlayer(player);
        final AbstractContainerMenu current = p.containerMenu;
        p.connection.send(new ClientboundOpenScreenPacket(current.containerId, containerType, new TextComponent(title)));
        try {
            Field titleField = AbstractContainerMenu.class.getDeclaredField("title");
            titleField.trySetAccessible();
            titleField.set(current, new TextComponent(title));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}