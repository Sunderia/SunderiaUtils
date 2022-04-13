package fr.sunderia.sunderiautils.enchantments;

import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.utils.ItemStackUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @deprecated Use {@link fr.sunderia.sunderiautils.enchantments.CustomEnchantment} instead.
 * @since 1.3
 */
@Deprecated(forRemoval = true, since = "1.3")
@ApiStatus.ScheduledForRemoval(inVersion = "1.4")
public class CustomEnchantmentWrapper extends Enchantment implements Listener {

    @Deprecated(forRemoval = true, since = "1.3")
    public static class EnchantmentBuilder {

        private final String namespace;
        private Consumer<PlayerInteractEvent> interactEventConsumer;
        private Consumer<BlockBreakEvent> breakBlockConsumer;
        private Enchantment[] conflicts = new Enchantment[0];
        private int maxLevel = 1;
        private EnchantmentTarget target = EnchantmentTarget.BREAKABLE;

        public EnchantmentBuilder(String namespace) {
            this.namespace = namespace;
        }

        public EnchantmentBuilder setConflicts(Enchantment... enchantments) {
            this.conflicts = enchantments;
            return this;
        }

        public EnchantmentBuilder onInteract(Consumer<PlayerInteractEvent> consumer) {
            this.interactEventConsumer = consumer;
            return this;
        }

        public EnchantmentBuilder onBreakBlock(Consumer<BlockBreakEvent> consumer) {
            this.breakBlockConsumer = consumer;
            return this;
        }

        public EnchantmentBuilder setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public EnchantmentBuilder setTarget(EnchantmentTarget target) {
            this.target = target;
            return this;
        }

        public Enchantment build() {
            return new CustomEnchantmentWrapper(namespace, WordUtils.capitalize(namespace, new char[]{'_'}).replace("_", ""),
                    maxLevel, target, interactEventConsumer, breakBlockConsumer, conflicts);
        }
    }

    private final String name;
    private final int maxLvl;
    private final EnchantmentTarget target;
    private final Enchantment[] conflicts;
    private final Consumer<PlayerInteractEvent> interactEventConsumer;

    private final Consumer<BlockBreakEvent> breakEventConsumer;

    public CustomEnchantmentWrapper(String namespace, String name, int maxLvl, EnchantmentTarget target, Consumer<PlayerInteractEvent> interactEventConsumer, Consumer<BlockBreakEvent> breakEventConsumer) {
        this(namespace, name, maxLvl, target, interactEventConsumer, breakEventConsumer, new Enchantment[0]);
    }

    public CustomEnchantmentWrapper(String namespace, String name, int maxLvl, EnchantmentTarget target, Consumer<PlayerInteractEvent> interactEventConsumer, Consumer<BlockBreakEvent> breakEventConsumer, Enchantment... conflicts) {
        super(NamespacedKey.minecraft(namespace));
        this.name = name;
        this.maxLvl = maxLvl;
        this.target = target;
        this.conflicts = conflicts;
        this.interactEventConsumer = interactEventConsumer;
        this.breakEventConsumer = breakEventConsumer;
        Bukkit.getServer().getPluginManager().registerEvents(this, SunderiaUtils.getPlugin());
        if(Arrays.stream(Enchantment.values()).anyMatch(e -> e.getKey().equals(this.getKey()))) return;
        registerEnchantment(this);
    }

    /**
     * Register {@link Enchantment enchantments}.
     * @param enchantment An {@link Enchantment enchantment} to register.
     * @throws RuntimeException if the {@link Enchantment} can't be registered
     */
    public static void registerEnchantment(@NotNull Enchantment enchantment) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLvl;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return target;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return Arrays.asList(this.conflicts).contains(other);
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return target.includes(item);
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        if(ItemStackUtils.isAirOrNull(event.getItem()) || !event.getItem().getItemMeta().hasEnchant(this) || interactEventConsumer == null) return;
        interactEventConsumer.accept(event);
    }

    @EventHandler
    public void onBreakEvent(BlockBreakEvent event) {
        var is = event.getPlayer().getInventory().getItemInMainHand();
        if(ItemStackUtils.isAirOrNull(is) || !is.getItemMeta().hasEnchant(this) || breakEventConsumer == null) return;
        breakEventConsumer.accept(event);
    }
}