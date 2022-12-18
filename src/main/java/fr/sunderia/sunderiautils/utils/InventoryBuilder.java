package fr.sunderia.sunderiautils.utils;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import fr.sunderia.sunderiautils.SunderiaUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Beta
public class InventoryBuilder {

    private final int spacing;
    private int rows;
    private String name;
    private final List<ItemStack> itemStacks;
    private final InventoryListeners listeners;

    public static final class InventoryListeners {
        private BukkitTask task;
        private int runnableTime = 20;
        private int runnableDelay = 20;
        private Consumer<InventoryClickEvent> clickEventConsumer = (l) -> {};
        private Consumer<InventoryOpenEvent> openEventConsumer = (l) -> {};
        private Consumer<InventoryCloseEvent> closeEventConsumer = (l) -> {};
        private Consumer<InventoryEvent> updateEventConsumer;
        private Consumer<InventoryDragEvent> dragEventConsumer = (l) -> {};
        private Gui inventory;
        public boolean closed = false;

        public int getRunnableTime() {
            return runnableTime;
        }

        public int getRunnableDelay() {
            return runnableDelay;
        }

        public Consumer<InventoryClickEvent> getClickEventConsumer() {
            return clickEventConsumer;
        }

        public Consumer<InventoryOpenEvent> getOpenEventConsumer() {
            return openEventConsumer;
        }

        public Consumer<InventoryCloseEvent> getCloseEventConsumer() {
            return closeEventConsumer;
        }

        public Consumer<InventoryEvent> getUpdateEventConsumer() {
            return updateEventConsumer;
        }

        public Consumer<InventoryDragEvent> getDragEventConsumer() {
            return dragEventConsumer;
        }

        private void setInventory(Gui inventory) {
            this.inventory = inventory;
        }

        private Gui getInventory() {
            return inventory;
        }

        public BukkitTask getTask() {
            return task;
        }

        public void setTask(BukkitTask task) {
            this.task = task;
        }

        public boolean atLeastOneNotNull() {
            return clickEventConsumer != null || this.closeEventConsumer != null || this.dragEventConsumer != null || this.updateEventConsumer != null || this.openEventConsumer != null;
        }
    }

    public static class Shape {
        private final String shape;
        private final int rows;
        private final Map<Character, ItemStack> itemMap;

        public Shape(String shape, Map<Character, ItemStack> itemMap) {
            this.shape = shape;
            if(shape.lines().map(line -> line.isEmpty() ? " ".repeat(9) : line).anyMatch(line -> line.length() != 9)) {
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

    public InventoryBuilder(@NotNull String name) {
        this(name, 9, 0);
    }

    public InventoryBuilder(@NotNull String name, int rows) {
        this(name, rows, 0);
    }

    public InventoryBuilder(@NotNull String name, int rows, int spacing) {
        this.name = name;
        this.itemStacks = Lists.newArrayList();
        this.rows = rows;
        this.spacing = spacing;
        this.listeners = new InventoryListeners();
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
        this.listeners = new InventoryListeners();
    }

    public InventoryBuilder onOpen(@NotNull Consumer<InventoryOpenEvent> consumer) {
        listeners.openEventConsumer = consumer;
        return this;
    }

    public InventoryBuilder onClose(@NotNull Consumer<InventoryCloseEvent> consumer) {
        listeners.closeEventConsumer = consumer;
        return this;
    }

    public InventoryBuilder onClick(@NotNull Consumer<InventoryClickEvent> consumer) {
        listeners.clickEventConsumer = consumer;
        return this;
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @return The builder
     */
    public InventoryBuilder onUpdate(Consumer<InventoryEvent> eventConsumer) {
        return onUpdate(eventConsumer, listeners.runnableTime, listeners.runnableDelay);
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
        listeners.updateEventConsumer = eventConsumer;
        listeners.runnableTime = time;
        listeners.runnableDelay = delay;
        return this;
    }

    public InventoryBuilder onDrag(@NotNull Consumer<InventoryDragEvent> consumer) {
        listeners.dragEventConsumer = consumer;
        return this;
    }

    public InventoryBuilder setRows(int rows) {
        if(rows > 6 || rows < 1) {
            SunderiaUtils.LOGGER.warn("Rows must be between 1 and 6, setting to 3");
            rows = 3;
        }
        this.rows = rows;
        return this;
    }

    public InventoryBuilder addItems(ItemStack... items) {
        return addItems(spacing, items);
    }

    public InventoryBuilder addItems(Material mat) {
        return addItems(spacing, new ItemStack(mat));
    }

    public InventoryBuilder addItems(int spacing, ItemStack... items) {
        List<ItemStack> stacks = new ArrayList<>();
        AtomicInteger i = new AtomicInteger();
        if(spacing > 0) {
            Arrays.stream(items).forEach(is -> {
                if (i.get() != 0 && i.get() % 9 == 0) {
                    stacks.add(null);
                    i.getAndIncrement();
                    return;
                }
                stacks.add(items[i.getAndIncrement()]);
                for (int j = 0; j < spacing; j++) {
                    stacks.add(null);
                }
            });
        } else {
            if(items == null) stacks.add(null);
            else stacks.addAll(Arrays.asList(items));
        }
        this.itemStacks.addAll(stacks);
        return this;
    }

    public InventoryBuilder addItems(int spacing, List<ItemStack> itemStacks) {
        return addItems(spacing, itemStacks.toArray(ItemStack[]::new));
    }

    public InventoryBuilder addItems(List<ItemStack> itemStacks) {
        return addItems(spacing, itemStacks.toArray(ItemStack[]::new));
    }

    public InventoryBuilder setItem(int i, ItemStack stack) {
        try {
            this.itemStacks.set(i, stack);
        } catch (IndexOutOfBoundsException e) {
            this.itemStacks.add(i, stack);
        }
        return this;
    }

    public InventoryBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public Gui build() {
        /* Inventory inv = Bukkit.createInventory(null, rows * 9, name);
        for (int i = 0; i < itemStacks.size(); i++) {
            inv.setItem(i, itemStacks.get(i));
        } */

        Gui gui = new Gui(this.name, this.rows, itemStacks, listeners);
        listeners.setInventory(gui);

        return gui;
    }
}