package fr.sunderia.sunderiautils.utils;

import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryBuilder implements Listener {

    private final int spacing;
    private int rows = 3;
    private String name;
    private final List<ItemStack> itemStacks;
    private Consumer<InventoryClickEvent> clickEventConsumer = InventoryEvent::getInventory;
    private Consumer<InventoryOpenEvent> openEventConsumer = InventoryEvent::getInventory;
    private Consumer<InventoryEvent> updateEventConsumer;
    private BukkitRunnable runnable;
    //1 Second
    private int runnableTime = 20;
    private int runnableDelay = 20;
    private boolean cancelEvent = false;


    public static class Shape {
        private final String shape;
        private final int rows;
        private final Map<Character, ItemStack> itemMap;

        public Shape(String shape, Map<Character, ItemStack> itemMap) {
            this.shape = shape;
            if(shape.lines().anyMatch(line -> line.length() != 9)) {
                throw new IllegalArgumentException("Shape must be 9 characters long");
            }
            this.itemMap = itemMap;
            this.rows = (int) shape.lines().count();
        }

        public String getShape() {
            return shape;
        }

        public int getRows() {
            return rows;
        }

        public Map<Character, ItemStack> getItemMap() {
            return itemMap;
        }
    }

    /**
     * @param name The name of the inventory
     */
    public InventoryBuilder(@NotNull String name) {
        this.name = name;
        this.itemStacks = new ArrayList<>();
        this.spacing = 0;
    }

    /**
     * @param name The name of the inventory
     * @param rows The number of rows
     */
    public InventoryBuilder(@NotNull String name, int rows) {
        this.name = name;
        this.itemStacks = new ArrayList<>();
        this.setRows(rows);
        this.spacing = 0;
    }

    /**
     * @param name The name of the inventory
     * @param rows The number of rows
     * @param spacing The spacing between items
     */
    public InventoryBuilder(@NotNull String name, int rows, int spacing) {
        this.name = name;
        this.itemStacks = new ArrayList<>(rows * 9);
        this.setRows(rows);
        this.spacing = spacing;
    }

    public InventoryBuilder(@NotNull String name, @NotNull Shape shape) {
        this.name = name;
        ItemStack[] is = new ItemStack[shape.getRows() * 9];
        this.spacing = 0;
        String shapeStr = shape.getShape();
        this.rows = shape.getRows();
        AtomicInteger index = new AtomicInteger(0);
        shapeStr.lines().forEach(line -> {
            for(int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if(c == ' ' || shape.getItemMap().get(c) == null) {
                    is[index.getAndIncrement()] = new ItemStack(Material.AIR);
                } else {
                    is[index.getAndIncrement()] = shape.getItemMap().get(c);
                }
            }
        });
        this.itemStacks = Arrays.asList(is);
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @return The builder
     */
    public InventoryBuilder onOpen(Consumer<InventoryOpenEvent> eventConsumer) {
        this.openEventConsumer = eventConsumer;
        return this;
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @return The builder
     */
    public InventoryBuilder onClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.clickEventConsumer = eventConsumer;
        return this;
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @return The builder
     */
    public InventoryBuilder onUpdate(Consumer<InventoryEvent> eventConsumer) {
        return onUpdate(eventConsumer, runnableTime, runnableDelay);
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @param time The time in ticks between each update
     * @return The builder
     */
    public InventoryBuilder onUpdate(Consumer<InventoryEvent> eventConsumer, int time) {
        return onUpdate(eventConsumer, time, time);
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @param time The time in ticks between each update
     * @param delay The delay in ticks before the first update
     * @return The builder
     */
    public InventoryBuilder onUpdate(Consumer<InventoryEvent> eventConsumer, int delay, int time) {
        this.updateEventConsumer = eventConsumer;
        this.runnableTime = time;
        this.runnableDelay = delay;
        return this;
    }

    /**
     * Cancel the click event if {@link #cancelEvent} is true
     * @return The builder
     */
    public InventoryBuilder setCancelled() {
        this.cancelEvent = !cancelEvent;
        return this;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST || event.getInventory().getSize() != getSize() || !event.getView().getTitle().equalsIgnoreCase(name)) return;
        event.setCancelled(cancelEvent);
        this.clickEventConsumer.accept(event);
    }

    @EventHandler
    private void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST || event.getInventory().getSize() != getSize() || !event.getView().getTitle().equalsIgnoreCase(name)) return;
        if (runnable == null && updateEventConsumer != null) {
            this.runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    updateEventConsumer.accept(event);
                }
            };
            runnable.runTaskTimer(SunderiaUtils.getPlugin(), runnableDelay, runnableTime);
        }
        this.openEventConsumer.accept(event);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST || event.getInventory().getSize() != getSize() || !event.getView().getTitle().equalsIgnoreCase(name) || runnable == null) return;
        this.runnable.cancel();
        this.runnable = null;
    }

    /**
     * @param rows Sets the amount of rows
     * @return The builder
     */
    public InventoryBuilder setRows(int rows) {
        if(rows > 6 || rows < 1) rows = 3;
        this.rows = rows;
        return this;
    }

    /**
     * Add an item to the inventory
     * @param itemStacks An array of {@link ItemStack}
     * @return The builder
     */
    public InventoryBuilder addItems(ItemStack... itemStacks) {
        return addItems(spacing, itemStacks);
    }

    /**
     * Add items to the inventory
     * @param spacing The spacing between items
     * @param mat The material of the item
     * @return The builder
     */
    public InventoryBuilder addItems(int spacing, Material mat) {
        return addItems(spacing, new ItemStack(mat));
    }

    /**
     * Add items to the inventory
     * @param spacing The spacing between items
     * @param itemStacks An array of {@link ItemStack}
     * @return The builder
     */
    public InventoryBuilder addItems(int spacing, ItemStack... itemStacks) {
        List<ItemStack> stacks = new ArrayList<>();
        AtomicInteger i = new AtomicInteger();
        if(spacing > 0) {
            Arrays.stream(itemStacks).forEach(is -> {
                if (i.get() != 0 && i.get() % 9 == 0) {
                    stacks.add(null);
                    i.getAndIncrement();
                    return;
                }
                stacks.add(itemStacks[i.getAndIncrement()]);
                for (int j = 0; j < spacing; j++) {
                    stacks.add(null);
                }
            });
        } else {
            if(itemStacks == null) {
                stacks.add(null);
            } else {
                stacks.addAll(Arrays.asList(itemStacks));
            }
        }
        this.itemStacks.addAll(stacks);
        return this;
    }

    /**
     * Add items to the inventory
     * @param itemStacks A list of {@link ItemStack}
     * @return The builder
     */
    public InventoryBuilder addItems(List<ItemStack> itemStacks) {
        return addItems(itemStacks.toArray(itemStacks.toArray(new ItemStack[0])));
    }

    /**
     * Add an item to a specific slot in the inventory
     * @param i The slot
     * @param stack The item
     * @return The builder
     */
    public InventoryBuilder setItem(int i, ItemStack stack) {
        try {
            this.itemStacks.set(i, stack);
        } catch (IndexOutOfBoundsException e) {
            this.itemStacks.add(i, stack);
        }
        return this;
    }

    /**
     * @param name The name of the inventory
     * @return The builder
     */
    public InventoryBuilder setName(String name) {
        this.name = name;
        return this;
    }

    private int getSize() {
        return rows * 9;
    }

    /**
     * This method will create the inventory and register the events.
     * @return An {@link Inventory}
     */
    public Inventory build() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SunderiaUtils.getPlugin());
        Inventory inv = Bukkit.createInventory(null, rows * 9, name);
        for (int i = 0; i < itemStacks.size(); i++) {
            inv.setItem(i, itemStacks.get(i));
        }
        return inv;
    }

}
