package fr.sunderia.sunderiautils.utils;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

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
     * @param is The item to check
     * @return {@code true} if the item is an armor
     */
    public static boolean isAnArmor(ItemStack is) {
        return is.getType().name().endsWith("_HELMET") || is.getType().name().endsWith("_CHESTPLATE") || is.getType().name().endsWith("_LEGGINGS") ||
                is.getType().name().endsWith("_BOOTS");
    }

    /**
     * This method check if the iteam is a tool or a weapon.
     * @param mat The material to check
     * @return {@code true} if the material is a tool or a weapon.
     */
    public static boolean isToolOrWeapon(Material mat) {
        return mat.name().endsWith("_SWORD") || mat.name().endsWith("_PICKAXE") || mat.name().endsWith("_AXE") || mat.name().endsWith("_HOE") ||
                mat.name().endsWith("_SHOVEL");
    }

    public static boolean isCustomItem(ItemStack item) {
        if(!hasLore(item)) return false;
        ItemMeta im = item.clone().getItemMeta();
        String lore = im.getLore().get(im.getLore().size() - 1);
        return ChatColor.stripColor(lore).startsWith(SunderiaUtils.getPlugin().getName() + ":");
    }

    /**
     * This method check if both item are the same.
     * @param first The first item
     * @param second The second item
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
     * This method use the lore to check if both items have the same name.
     * This method only works on custom items.
     * @param first The first item
     * @param second The second item
     * @return {@code true} if the two items are the same
     */
    public static boolean isSameItem(ItemStack first, ItemStack second) {
        /*if(!hasLore(first) || !hasLore(second)) return false;
        ItemMeta im1 = first.clone().getItemMeta();
        ItemMeta im2 = second.clone().getItemMeta();
        String lore = im1.getLore().get(im1.getLore().size() - 1);
        String lore2 = im2.getLore().get(im2.getLore().size() - 1);
        if(!ChatColor.stripColor(lore).startsWith(SunderiaUtils.getPlugin().getName() + ":") ||
                !ChatColor.stripColor(lore2).startsWith(SunderiaUtils.getPlugin().getName() + ":")) return false;*/
        if(!isCustomItem(first) || !isCustomItem(second)) return false;
        return first.getItemMeta().getLore().equals(second.getItemMeta().getLore());
    }

    /**
     * @param item The item to check
     * @return {@code true} if the item is an air or null
     */
    public static boolean isAirOrNull(ItemStack item){
        return item == null || item.getType().isAir();
    }

    /**
     * @param is The item to check
     * @return {@code true} if the item is not null and not air.
     * @see #isAirOrNull(ItemStack)
     */
    public static boolean isNotAirNorNull(ItemStack is) {
        return !isAirOrNull(is);
    }

    /**
     * @param is The item to check
     * @return {@code true} if the item has lore
     */
    public static boolean hasLore(ItemStack is) {
        return !isAirOrNull(is) && is.hasItemMeta() && is.getItemMeta().hasLore();
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
}