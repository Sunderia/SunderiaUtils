package fr.sunderia.sunderiautils.utils;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Arrays;
import java.util.List;

public class ItemStackUtils {

    private ItemStackUtils() {}

    /**
     * An instance of {@link ItemStack} that contains {@link Material#AIR AIR}
     */
    public static final ItemStack EMPTY = new ItemStack(Material.AIR);

    /**
     * This method check if the item is an armor.
     * @param itemStack the itemStack checked
     * @return {@code true} if the item is an armor
     */
    public static boolean isAnArmor(ItemStack itemStack) {
        return itemStack.getType().name().endsWith("_HELMET") || itemStack.getType().name().endsWith("_CHESTPLATE") || itemStack.getType().name().endsWith("_LEGGINGS") ||
                itemStack.getType().name().endsWith("_BOOTS");
    }

    /**
     * This method check if the iteam is a tool or a weapon.
     * @param material The material to check
     * @return {@code true} if the material is a tool or a weapon.
     */
    public static boolean isToolOrWeapon(Material material) {
        return material.name().endsWith("_SWORD") || material.name().endsWith("_PICKAXE") || material.name().endsWith("_AXE") || material.name().endsWith("_HOE") ||
                material.name().endsWith("_SHOVEL");
    }

    /**
     * @param first The first itemStack
     * @param second The second itemStack
     * @return {@code true} if the two itemStack are the same
     * @see #isSameItem(ItemStack, ItemStack)
     */
    public static boolean isSimilar(ItemStack first, ItemStack second) {
        if(isAirOrNull(first) || isAirOrNull(second)) return false;
        ItemMeta im1 = first.getItemMeta();
        ItemMeta im2 = second.getItemMeta();
        if(im1 instanceof Damageable dmg1 && im2 instanceof Damageable dmg2) {
            dmg1.setDamage(dmg2.getDamage());
        }
        first.setItemMeta(im1);
        second.setItemMeta(im2);
        return first.isSimilar(second);
    }

    /**
     * @param itemStack the itemStack
     * @return {@code true} if the itemStack is a custom item
     */
    public static boolean isCustomItem(ItemStack itemStack) {
        if(!hasLore(itemStack)) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        String lore = itemMeta.getLore().get(itemMeta.getLore().size() - 1);
        return ChatColor.stripColor(lore).startsWith(SunderiaUtils.getPlugin().getName().toLowerCase() + ":");
    }

    /**
     * This method use the lore to check if both items have the same name.
     * This method only works on custom items.
     * @param first The first item
     * @param second The second item
     * @return {@code true} if the two items are the same
     */
    public static boolean isSameCustomItem(ItemStack first, ItemStack second) {
        if(!isCustomItem(first) || !isCustomItem(second)) return false;
        return first.getItemMeta().getLore().equals(second.getItemMeta().getLore());
    }
    
    public static boolean isSameItem(ItemStack first, ItemStack second) {
        return ItemStackUtils.isCustomItem(first) && ItemStackUtils.isCustomItem(second) ? ItemStackUtils.isSameCustomItem(first, second) : ItemStackUtils.isSimilar(first, second);
    }

    /**
     * @param itemStack The item to check
     * @return {@code true} if the item is an air or null
     */
    public static boolean isAirOrNull(ItemStack itemStack){
        return itemStack == null || itemStack.getType().isAir();
    }

    /**
     * @param itemStack The item to check
     * @return {@code true} if the item is not null and not air.
     * @see #isAirOrNull(ItemStack)
     */
    public static boolean isNotAirNorNull(ItemStack itemStack) {
        return !isAirOrNull(itemStack);
    }

    /**
     * @param itemStack The item to check
     * @return {@code true} if the item has lore
     */
    public static boolean hasLore(ItemStack itemStack) {
        return !isAirOrNull(itemStack) && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore();
    }

    /**
     * @return A random banner.
     */
    public static Material randomBanner() {
        List<Material> banners = Arrays.stream(Material.values()).filter(banner -> banner.name().endsWith("_BANNER") && !banner.name().endsWith("_WALL_BANNER") &&
                !banner.name().startsWith("LEGACY")).toList();
        return banners.get(SunderiaUtils.getRandom().nextInt(banners.size()));
    }

    /**
     * @return A random skull like {@link Material#SKELETON_SKULL}.
     */
    public static Material randomSkull() {
        List<Material> skulls = Arrays.stream(Material.values()).filter(material -> (!material.name().startsWith("PISTON") && material.name().endsWith("_HEAD") && !material.name().endsWith("_WALL_HEAD")) ||
                (material.name().endsWith("_SKULL") && !material.name().startsWith("LEGACY") && !material.name().endsWith("_WALL_SKULL"))).toList();
        return skulls.get(SunderiaUtils.getRandom().nextInt(skulls.size()));
    }
    
    /**
     * @param itemStack the itemStack
     * @return {@code true} if the itemStack has the namespacedKey with the right persistentDataType
     */
    public static <T, Z> boolean hasPersistentDataContainer(@Nullable ItemStack itemStack, NamespacedKey namespacedKey, PersistentDataType<T, Z> persistentDataType){
        return (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(namespacedKey, persistentDataType));
    }
    
    /**
     * @param itemStack the itemStack
     * @param clone if the itemStack should be cloned or not
     * @return the same itemStack (cloned or not) but without all his PersitentDataContainer
     */
    public static ItemStack removeAllPersitentDataContainer(ItemStack itemStack, boolean clone){
        ItemStack item = clone ? itemStack.clone() : itemStack;
        if(item.hasItemMeta()){
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.getPersistentDataContainer().getKeys().forEach(key -> itemMeta.getPersistentDataContainer().remove(key));
            item.setItemMeta(itemMeta);
        }
        return item;
    }
    
    /**
     * @param itemStack the itemStack
     * @return the same itemStack but without all his PersitentDataContainer
     */
    public static ItemStack removeAllPersitentDataContainer(ItemStack itemStack){
        return removeAllPersitentDataContainer(itemStack, false);
    }
    
    /**
     * @param itemStack the itemStack
     * @param clone if the itemStack should be cloned or not
     * @return the same itemStack (cloned or not) but without all his lore
     */
    public static ItemStack removeAllLore(ItemStack itemStack, boolean clone){
        ItemStack item = clone ? itemStack.clone() : itemStack;
        if(item.hasItemMeta() && !item.getItemMeta().getLore().isEmpty()){
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(new ArrayList<>());
            item.setItemMeta(itemMeta);
        }
        return item;
    }
    
    /**
     * @param itemStack the itemStack
     * @return the same itemStack but without all his PersitentDataContainer
     */
    public static ItemStack removeAllLore(ItemStack itemStack){
        return removeAllLore(itemStack, false);
    }

    /**
     * @param enchantment the enchantment
     * @param itemStack the itemStack
     * @return {@code true} if the itemStack has the enchantment
     */
    public static boolean hasEnchantment(Enchantment enchantment, ItemStack itemStack) {
        if(ItemStackUtils.isAirOrNull(itemStack) || !itemStack.hasItemMeta()) return false;
        return itemStack.getItemMeta().hasEnchant(enchantment);
    }
}
