package fr.sunderia.sunderiautils.enchantments;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


import java.util.Arrays;
import java.util.function.Consumer;

public class CustomEnchantmentWrapper extends Enchantment implements Listener {

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
    }

    @NotNull
    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
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
        if(interactEventConsumer == null) return;
        interactEventConsumer.accept(event);
    }

    @EventHandler
    public void onBreakEvent(BlockBreakEvent event) {
        if(breakEventConsumer == null) return;
        breakEventConsumer.accept(event);
    }
}