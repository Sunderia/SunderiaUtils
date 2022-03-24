package fr.sunderia.sunderiautils.utils;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder implements Listener {

    private ItemStack stack;
    private Consumer<PlayerInteractEvent> interactConsumer;

    /**
     * @param stack The {@link ItemStack} to copy
     */
    public ItemBuilder(ItemStack stack) {
        this.stack = stack.clone();
    }

    /**
     * @param mat The {@link Material} of the item
     * @param amount The amount of items
     */
    public ItemBuilder(Material mat, int amount) {
        this(new ItemStack(mat, amount));
    }

    /**
     * @param mat The {@link Material} of the item
     */
    public ItemBuilder(Material mat) {
        this(mat, 1);
    }

    /**
     * @return The {@link ItemMeta} of the item.
     */
    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    /**
     * Set the color of the leather armor
     * This method will only work if the item is a leather armor
     * @param color The color of the armor
     * @return the ItemBuilder
     */
    public ItemBuilder setColor(Color color) {
        if(!(stack.getItemMeta() instanceof LeatherArmorMeta meta)) return this;
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    /**
     * This method add a enchantment depending on the item.
     * For example if the item is an armor it will add {@link Enchantment#ARROW_KNOCKBACK} else it will add {@link Enchantment#PROTECTION_EXPLOSIONS}.
     * @param glow If the item should glow
     * @return The ItemBuilder
     */
    public ItemBuilder setGlow(boolean glow) {
        if(glow) {
            addEnchant(ItemStackUtils.isAnArmor(stack) ? Enchantment.ARROW_KNOCKBACK : Enchantment.PROTECTION_EXPLOSIONS, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            meta.getEnchants().keySet().forEach(meta::removeEnchant);
        }
        return this;
    }

    /**
     * @param eventConsumer The {@link Consumer} of the event
     * @return The ItemBuilder
     */
    public ItemBuilder onInteract(Consumer<PlayerInteractEvent> eventConsumer) {
        this.interactConsumer = eventConsumer;
        return this;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if(!ItemStackUtils.isSameItem(event.getItem(), this.stack) || interactConsumer == null) return;
        interactConsumer.accept(event);
    }

    /**
     * This method check if the item is already glowing, if it is it will remove the glow else it will add it.
     * @return The ItemBuilder
     */
    public ItemBuilder setGlow() {
        return setGlow(stack.getEnchantments().isEmpty());
    }

    /**
     * @param unbreakable If the item should be unbreakable
     * @return The ItemBuilder
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof Damageable)) return this;
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * @param amount The amount of items
     * @return The ItemBuilder
     */
    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    /**
     * This method will replace the current {@link ItemMeta} with the new one.
     * @param meta An instance of {@link ItemMeta}
     * @return The ItemBuilder
     */
    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * @param player An instance of {@link OfflinePlayer}
     * @return The ItemBuilder
     */
    public ItemBuilder setHead(OfflinePlayer player) {
        if(!(stack.getItemMeta() instanceof SkullMeta meta) || !meta.hasOwner()) return this;
        meta.setOwningPlayer(player);
        setItemMeta(meta);
        return this;
    }

    /**
     * @param displayName The display name of the item
     * @return The ItemBuilder
     */
    public ItemBuilder setDisplayName(String displayName) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayName);
        setItemMeta(meta);
        return this;
    }

    /**
     * This method will replace the current itemstack with the new one.
     * @param stack An {@link ItemStack}
     * @return The ItemBuilder
     */
    public ItemBuilder setItemStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    /**
     * This method will replace the current lore with the new one.
     * @param lore A list of {@link String}.
     * @return The ItemBuilder
     */
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    /**
     * This method will replace the current lore with the new one.
     * @param lore A {@link String}.
     * @return The ItemBuilder
     */
    public ItemBuilder setLore(String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    /**
     * @param enchantment An {@link Enchantment}
     * @param level The level of the enchantment
     * @return The ItemBuilder
     */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    /**
     * This method will add every protection enchantments to the item.
     * @param level The level of the enchantments.
     * @return The ItemBuilder
     */
    public ItemBuilder addProtection(int level) {
        return addEnchant(Enchantment.PROTECTION_EXPLOSIONS, level)
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, level)
                .addEnchant(Enchantment.PROTECTION_FIRE, level)
                .addEnchant(Enchantment.PROTECTION_PROJECTILE, level);
    }

    /**
     * @param flag An {@link ItemFlag}
     * @return The ItemBuilder
     */
    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    /**
     * @param flags An array of {@link ItemFlag}
     * @return The ItemBuilder
     */
    public ItemBuilder addItemFlag(ItemFlag... flags) {
        Arrays.stream(flags).forEach(this::addItemFlag);
        return this;
    }

    /**
     * @param customModelData The id of the custom model data
     * @return The ItemBuilder
     */
    public ItemBuilder setCustomModelData(int customModelData) {
        ItemMeta meta = getItemMeta();
        meta.setCustomModelData(customModelData);
        setItemMeta(meta);
        return this;
    }

    /**
     * This method will register the events, add a line to the lore and set the item meta.
     * @return The {@link ItemStack}
     */
    public ItemStack build() {
        if(interactConsumer != null) Bukkit.getServer().getPluginManager().registerEvents(this, SunderiaUtils.getPlugin());
        ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        String l = ChatColor.DARK_GRAY + SunderiaUtils.getPlugin().getName().toLowerCase() + ":" +
                ChatColor.stripColor(getItemMeta().getDisplayName().replaceAll("\\s+", "_").toLowerCase());
        lore.removeIf(s -> s.equalsIgnoreCase(l));
        lore.add(l);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
}
