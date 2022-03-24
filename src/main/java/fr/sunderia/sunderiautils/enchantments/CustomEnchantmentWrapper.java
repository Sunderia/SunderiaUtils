package fr.sunderia.sunderiautils.enchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


import java.util.Arrays;

public class CustomEnchantmentWrapper extends Enchantment {

    private final String name;
    private final int maxLvl;
    private final EnchantmentTarget target;
    private final Enchantment[] conflicts;

    public CustomEnchantmentWrapper(String namespace, String name, int maxLvl, EnchantmentTarget target) {
        this(namespace, name, maxLvl, target, new Enchantment[0]);
    }

    public CustomEnchantmentWrapper(String namespace, String name, int maxLvl, EnchantmentTarget target, Enchantment... conflicts) {
        super(NamespacedKey.minecraft(namespace));
        this.name = name;
        this.maxLvl = maxLvl;
        this.target = target;
        this.conflicts = conflicts;
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
}