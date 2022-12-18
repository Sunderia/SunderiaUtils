package fr.sunderia.sunderiautils.utils;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.listeners.InventoryListener;
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
import java.util.function.BiConsumer;

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
        private BiConsumer<InventoryClickEvent, Gui> clickEventConsumer = (l, gui) -> {};
        private BiConsumer<InventoryOpenEvent, Gui> openEventConsumer = (l, gui) -> {};
        private BiConsumer<InventoryCloseEvent, Gui> closeEventConsumer = (e, gui) -> {};
        private BiConsumer<InventoryEvent, Gui> updateEventConsumer;
        private BiConsumer<InventoryDragEvent, Gui> dragEventConsumer = (l, gui) -> {};
        private Gui inventory;
        public boolean closed = false;

        public int getRunnableTime() {
            return runnableTime;
        }

        public int getRunnableDelay() {
            return runnableDelay;
        }

        public BiConsumer<InventoryClickEvent, Gui> getClickEventConsumer() {
            return clickEventConsumer;
        }

        public BiConsumer<InventoryOpenEvent, Gui> getOpenEventConsumer() {
            return openEventConsumer;
        }

        public BiConsumer<InventoryCloseEvent, Gui> getCloseEventConsumer() {
            return closeEventConsumer;
        }

        public BiConsumer<InventoryEvent, Gui> getUpdateEventConsumer() {
            return updateEventConsumer;
        }

        public BiConsumer<InventoryDragEvent, Gui> getDragEventConsumer() {
            return dragEventConsumer;
        }

        private void setInventory(Gui inventory) {
            this.inventory = inventory;
        }

        public Gui getInventory() {
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
            if(shape.lines().map(line -> line.isEmpty() ? " ".repeat(9) : line).anyMatch(line -> line.length() != 9)) {
                throw new IllegalArgumentException("Shape must be 9 characters long");
            }
            this.shape = shape;
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
        String[] lines = shapeStr.lines().toArray(String[]::new);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if(c == ' ' || shape.getItemMap().get(c) == null) {
                    is[i * 9 + j] = new ItemStack(Material.AIR);
                } else {
                    is[i * 9 + j] = shape.getItemMap().get(c);
                }
            }
        }
        this.itemStacks = Arrays.asList(is);
        this.listeners = new InventoryListeners();
    }

    public InventoryBuilder onOpen(@NotNull BiConsumer<InventoryOpenEvent, Gui> consumer) {
        listeners.openEventConsumer = consumer;
        return this;
    }

    public InventoryBuilder onClose(@NotNull BiConsumer<InventoryCloseEvent, Gui> consumer) {
        listeners.closeEventConsumer = consumer;
        return this;
    }

    public InventoryBuilder onClick(@NotNull BiConsumer<InventoryClickEvent, Gui> consumer) {
        listeners.clickEventConsumer = consumer;
        return this;
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @return The builder
     */
    public InventoryBuilder onUpdate(BiConsumer<InventoryEvent, Gui> eventConsumer) {
        return onUpdate(eventConsumer, listeners.runnableTime, listeners.runnableDelay);
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @param time The time in ticks between each update
     * @return The builder
     */
    public InventoryBuilder onUpdate(BiConsumer<InventoryEvent, Gui> eventConsumer, int time) {
        return onUpdate(eventConsumer, time, time);
    }

    /**
     * @param eventConsumer A consumer containing the event
     * @param time The time in ticks between each update
     * @param delay The delay in ticks before the first update
     * @return The builder
     */
    public InventoryBuilder onUpdate(BiConsumer<InventoryEvent, Gui> eventConsumer, int delay, int time) {
        listeners.updateEventConsumer = eventConsumer;
        listeners.runnableTime = time;
        listeners.runnableDelay = delay;
        return this;
    }

    public InventoryBuilder onDrag(@NotNull BiConsumer<InventoryDragEvent, Gui> consumer) {
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
        Gui gui = new Gui(this.name, this.rows, itemStacks, listeners);
        listeners.setInventory(gui);

        return gui;
    }
}