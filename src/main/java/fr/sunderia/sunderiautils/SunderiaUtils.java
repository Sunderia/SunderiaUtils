package fr.sunderia.sunderiautils;

import com.google.common.reflect.ClassPath;
import com.jeff_media.armorequipevent.ArmorEquipEvent;
import fr.sunderia.sunderiautils.commands.CommandInfo;
import fr.sunderia.sunderiautils.commands.PluginCommand;
import fr.sunderia.sunderiautils.listeners.CustomBlockListener;
import fr.sunderia.sunderiautils.listeners.PlayerListener;
import fr.sunderia.sunderiautils.listeners.RecipeListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class SunderiaUtils {

    private static JavaPlugin plugin;
    private static Random random;
    private static Random secureRandom;
    private static final Logger LOGGER = LoggerFactory.getLogger(SunderiaUtils.class);

    private SunderiaUtils(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new RecipeListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new CustomBlockListener(), plugin);
        ArmorEquipEvent.registerListener(plugin);
    }

    /**
     * Please call this method in the onEnable method of your plugin.
     * If you try to use any other method of this module it will throw a {@link NullPointerException} because the {@link SunderiaUtils#plugin} is null.
     * @param plugin Your plugin
     * @return An instance of {@link SunderiaUtils}
     */
    public static SunderiaUtils of(JavaPlugin plugin) {
        if(SunderiaUtils.plugin != null) {
            throw new UnsupportedOperationException("The plugin field is already set. Please call the of method only once.");
        }
        SunderiaUtils.plugin = plugin;
        random = new Random();
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            secureRandom = random;
        }
        return new SunderiaUtils(plugin);
    }

    /**
     * Register every command in the package
     * @param packageName The name of the package
     * @throws IOException If it can't read the classes in the package
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void registerCommands(String packageName) throws IOException {
        if(plugin == null) {
            throw new UnsupportedOperationException("Plugin not found");
        }
        ClassPath.from(getClassLoader())
                .getTopLevelClassesRecursive(packageName)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .filter(PluginCommand.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(CommandInfo.class))
                .forEach(clazz -> {
                    LOGGER.info("Registering command {}", clazz.getAnnotation(CommandInfo.class).name());
                    PluginCommand command = Objects.requireNonNull((PluginCommand) newInstance(clazz, true));
                    try {
                        ((SimpleCommandMap) plugin.getServer().getClass().getMethod("getCommandMap").invoke(plugin.getServer())).register(plugin.getDescription().getName(), command);
                    } catch (ReflectiveOperationException e) {
                        LOGGER.error("The method getCommandMap in CraftServer was not found", e);
                    }
                });
    }

    /**
     * Register every listener in the package.
     * It will skip every classes that does not have a constructor with no parameters.
     * @param packageName The name of the package
     * @throws IOException If it can't read the classes in the package
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void registerListeners(String packageName) throws IOException {
        if(plugin == null) {
            throw new UnsupportedOperationException("Plugin not found");
        }
        ClassPath.from(getClassLoader())
                .getTopLevelClassesRecursive(packageName)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .filter(Listener.class::isAssignableFrom)
                .filter(clazz -> {
                    try {
                        clazz.getConstructor();
                        return true;
                    } catch (NoSuchMethodException e) {
                        return false;
                    }
                })
                .forEach(clazz -> {
                    LOGGER.info("Registering listener {}", clazz.getSimpleName());
                    Bukkit.getPluginManager().registerEvents((Listener) Objects.requireNonNull(newInstance(clazz, false)), plugin);
                });
    }

    private static ClassLoader getClassLoader() {
        try {
            Method m = JavaPlugin.class.getDeclaredMethod("getClassLoader");
            m.setAccessible(true);
            return (ClassLoader) m.invoke(plugin);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object newInstance(Class<?> clazz, boolean command) {
        try {
            if(command) {
                var constructor = clazz.getConstructor(JavaPlugin.class);
                constructor.setAccessible(true);
                return constructor.newInstance(plugin);
            } else {
                var constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }

    /**
     * @return An instance of a plugin.
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return The instance of {@link Random}
     */
    public static Random getRandom() {
        return random;
    }

    /**
     * @return The instance of {@link SecureRandom}
     */
    @SuppressWarnings("unused")
    public static Random getSecureRandom() {
        return secureRandom;
    }
}
