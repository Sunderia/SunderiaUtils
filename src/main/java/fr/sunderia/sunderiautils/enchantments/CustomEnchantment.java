package fr.sunderia.sunderiautils.enchantments;

import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.utils.ItemStackUtils;
import fr.sunderia.sunderiautils.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomEnchantment implements Listener {

    //TODO: Convert this class to a json and load it when the plugin is enabled.

    private final String name;
    private final int level;
    private final Consumer<PlayerInteractEvent> interactEventConsumer;
    private final Consumer<BlockBreakEvent> breakEventConsumer;

    public CustomEnchantment(String name, int level, Consumer<PlayerInteractEvent> interactEventConsumer, Consumer<BlockBreakEvent> breakEventConsumer) {
        this.name = name;
        this.level = level;
        this.interactEventConsumer = interactEventConsumer;
        this.breakEventConsumer = breakEventConsumer;
        Bukkit.getPluginManager().registerEvents(this, SunderiaUtils.getPlugin());
    }


    public static class CustomEnchantmentBuilder {

        private final String name;
        private Consumer<PlayerInteractEvent> interactEventConsumer;
        private Consumer<BlockBreakEvent> breakEventConsumer;
        private int level = 1;

        public CustomEnchantmentBuilder(String name) {
            this.name = name.replaceAll("\\s+", "_").toLowerCase();
        }

        public CustomEnchantmentBuilder level(int level) {
            this.level = level;
            return this;
        }

        public CustomEnchantmentBuilder onInteract(Consumer<PlayerInteractEvent> interactEventConsumer) {
            this.interactEventConsumer = interactEventConsumer;
            return this;
        }

        public CustomEnchantmentBuilder onBreak(Consumer<BlockBreakEvent> breakEventConsumer) {
            this.breakEventConsumer = breakEventConsumer;
            return this;
        }

        public CustomEnchantment addOnItem(ItemStack is) {
            ItemMeta meta = is.getItemMeta();
            if(meta != null && !meta.getPersistentDataContainer().has(SunderiaUtils.key("enchantment-" + name), PersistentDataType.INTEGER)) {
                meta.getPersistentDataContainer().set(SunderiaUtils.key("enchantment-" + name), PersistentDataType.INTEGER, level);
                List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
                lore.add(0, ChatColor.GRAY + StringUtils.capitalizeWord(name) + " " + StringUtils.integerToRoman(level));
                meta.setLore(lore);
                is.setItemMeta(meta);
                is.addUnsafeEnchantment(ItemStackUtils.isAnArmor(is) ? Enchantment.ARROW_KNOCKBACK : Enchantment.PROTECTION_EXPLOSIONS, 1);
            }
            return new CustomEnchantment(name, level, interactEventConsumer, breakEventConsumer);
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        if(ItemStackUtils.isAirOrNull(event.getItem()) ||
                event.getItem().getItemMeta() == null ||
                !event.getItem().getItemMeta().getPersistentDataContainer().has(SunderiaUtils.key("enchantment-" + name), PersistentDataType.INTEGER)
                || interactEventConsumer == null) return;
        interactEventConsumer.accept(event);
    }

    @EventHandler
    public void onBreakEvent(BlockBreakEvent event) {
        var is = event.getPlayer().getInventory().getItemInMainHand();
        if(ItemStackUtils.isAirOrNull(is) ||
                is.getItemMeta() == null ||
                !is.getItemMeta().getPersistentDataContainer().has(SunderiaUtils.key("enchantment-" + name), PersistentDataType.INTEGER)
                || breakEventConsumer == null) return;
        breakEventConsumer.accept(event);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}