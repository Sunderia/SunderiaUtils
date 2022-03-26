package fr.sunderia.sunderiautils.utils;

import fr.sunderia.sunderiautils.enchantments.CustomEnchantmentWrapper;
import org.apache.commons.lang.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.Consumer;

public class EnchantmentBuilder {

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
