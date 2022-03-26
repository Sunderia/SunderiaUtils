package fr.sunderia.sunderiautils;

import com.google.common.reflect.ClassPath;
import com.jeff_media.armorequipevent.ArmorEquipEvent;
import fr.sunderia.sunderiautils.commands.CommandInfo;
import fr.sunderia.sunderiautils.commands.PluginCommand;
import fr.sunderia.sunderiautils.listeners.RecipeListener;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class SunderiaUtils {

    private static JavaPlugin plugin;
    private static Random random;
    private static final Logger LOGGER = LoggerFactory.getLogger(SunderiaUtils.class);

    private SunderiaUtils(JavaPlugin plugin) {
        SunderiaUtils.plugin = plugin;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            random = new Random();
        }
        Bukkit.getPluginManager().registerEvents(new RecipeListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new RecipeListener(), plugin);
        ArmorEquipEvent.registerListener(plugin);
    }

    /**
     * Please call this method in the onEnable method of your plugin.
     * If you try to use any other method of this module it will throw a {@link NullPointerException} because the {@link SunderiaUtils#plugin} is null.
     * @param plugin Your plugin
     * @return An instance of {@link SunderiaUtils}
     */
    public static SunderiaUtils of(JavaPlugin plugin) {
        return new SunderiaUtils(plugin);
    }

    /**
     * Register every commands in the package
     * @param packageName The name of the package
     * @throws IOException If it can't read the classes in the package
     */
    public static void registerCommands(String packageName) throws IOException {
        if(plugin == null) {
            throw new RuntimeException("Plugin not found");
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
                    SimpleCommandMap map = ((CraftServer) plugin.getServer()).getCommandMap();
                    map.register(plugin.getDescription().getName(), command);
                });
    }

    /**
     * Register {@link Enchantment enchantments}.
     * @param enchantments An array of {@link Enchantment enchantments}
     * @throws RuntimeException if the {@link Enchantment} can't be registered
     */
    public static void registerEnchantments(Enchantment... enchantments) {
        Arrays.stream(enchantments).forEach(ench -> {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
                Enchantment.registerEnchantment(ench);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Register every listeners in the package.
     * It will skip every classes that does not have a constructor with no parameters.
     * @param packageName The name of the package
     * @throws IOException If it can't read the classes in the package
     */
    public static void registerListeners(String packageName) throws IOException {
        if(plugin == null) {
            throw new RuntimeException("Plugin not found");
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

    private static <T> Class<? extends T> getClassByName(String className, Class<T> extendsClass) {
        try {
            Class<?> aClass = Class.forName(className);
            if(extendsClass.isAssignableFrom(aClass)) {
                return (Class<? extends T>) aClass;
            }
        } catch (ClassNotFoundException e) {}
        return null;
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
}
