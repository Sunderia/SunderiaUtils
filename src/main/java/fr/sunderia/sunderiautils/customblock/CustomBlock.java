package fr.sunderia.sunderiautils.customblock;

import com.google.common.collect.ImmutableList;
import fr.sunderia.sunderiautils.SunderiaUtils;
import fr.sunderia.sunderiautils.utils.ItemBuilder;
import fr.sunderia.sunderiautils.utils.ReflectionUtils;
import fr.sunderia.sunderiautils.utils.StringUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SerializableAs("customblock")
public class CustomBlock implements Cloneable, ConfigurationSerializable {

    private static final List<CustomBlock> REGISTERED_BLOCKS = new ArrayList<>();
    public static final NamespacedKey KEY = SunderiaUtils.key("custom_block");
    public static final NamespacedKey CMD_KEY = SunderiaUtils.key("custom_model_data");

    public static class Builder {

        private final NamespacedKey key;
        private final int cmd;

        private Material mat;
        private ItemStack[] drop;
        private int xp;
        private Location loc;

        public Builder(NamespacedKey key, int cmd) {
            this.key = key;
            this.cmd = cmd;
        }

        public Builder setMaterial(Material mat) {
            this.mat = mat;
            return this;
        }

        public Builder setDrops(ItemStack... drop) {
            this.drop = drop;
            return this;
        }

        public Builder setXp(int xp) {
            this.xp = xp;
            return this;
        }

        public Builder setLocation(Location loc) {
            this.loc = loc;
            return this;
        }

        public CustomBlock build() {
            return new CustomBlock(key, mat, cmd, xp, loc, drop);
        }

    }

    private final NamespacedKey name;
    private final int customModelData;
    private final Material mat;
    private final ItemStack[] drop;
    private final int xp;
    private final ItemStack item;
    private Location loc;

    public CustomBlock(NamespacedKey name, Material mat, int customModelData, int xp, Location loc, ItemStack... drop) {
        this.name = Objects.requireNonNull(name, "The namespace key cannot be null");
        this.mat = mat == null ? Material.LIME_GLAZED_TERRACOTTA : mat;
        this.customModelData = customModelData;
        this.drop = drop == null ? new ItemStack[0] : drop;
        this.xp = xp;
        this.loc = loc;
        this.item = new ItemBuilder(this.mat)
                .setDisplayName(StringUtils.capitalizeWord(name.getKey().replace('_', ' ').toLowerCase()))
                .setCustomModelData(customModelData).build();
        REGISTERED_BLOCKS.add(this);
    }

    @SuppressWarnings("unchecked")
    public CustomBlock(Map<String, Object> serializedCustomBlock) {
        this((NamespacedKey) serializedCustomBlock.get("name"), Material.valueOf((String) serializedCustomBlock.get("mat")),
                (int) serializedCustomBlock.get("cmd"), (int) serializedCustomBlock.get("xp"), (Location) serializedCustomBlock.get("loc"),
                ((List<ItemStack>) serializedCustomBlock.get("drops")).toArray(new ItemStack[0]));
    }

    public CustomBlock(NamespacedKey name, Material mat, int customModelData, List<ItemStack> drop, int xp) {
        this(name, mat, customModelData, xp, null, drop.toArray(new ItemStack[0]));
    }

    public CustomBlock(NamespacedKey name, Material mat, int customModelData, int xp, ItemStack... drop) {
        this(name, mat, customModelData, Arrays.asList(drop), xp);
    }

    public CustomBlock(NamespacedKey name, int customModelData, int xp, List<ItemStack> drop) {
        this(name, Material.LIME_GLAZED_TERRACOTTA, customModelData, drop, xp);
    }

    public CustomBlock(NamespacedKey name, int customModelData, int xp, ItemStack... drop) {
        this(name, Material.LIME_GLAZED_TERRACOTTA, customModelData, Arrays.asList(drop), xp);
    }

    public CustomBlock(NamespacedKey name, int customModelData, List<ItemStack> drop) {
        this(name, customModelData, 0, drop);
    }

    public CustomBlock(NamespacedKey name, int customModelData, ItemStack... drop) {
        this(name, customModelData, 0, drop);
    }

    public void setBlock(Location loc) {
        if (loc.getWorld() == null) return;
        loc.getBlock().setBlockData(Material.SPAWNER.createBlockData());
        if (!(loc.getBlock().getState() instanceof CreatureSpawner block)) return;
        block.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, name.getNamespace() + ":" + name.getKey());
        block.getPersistentDataContainer().set(CMD_KEY, PersistentDataType.INTEGER, this.getCustomModelData());
        block.update();
        var craftWorld = ReflectionUtils.getCraftBukkitClass("CraftWorld").cast(loc.getWorld());
        ServerLevel server;
        try {
            server = (ServerLevel) ReflectionUtils.getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(craftWorld);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        BlockPos pos = new BlockPos(loc.getX(), loc.getY(), loc.getZ());
        SpawnerBlockEntity spawnerTE = (SpawnerBlockEntity) server.getBlockEntity(pos);
        BaseSpawner spawner = spawnerTE.getSpawner();
        CompoundTag spawnData = new CompoundTag();
        ListTag armorList = new ListTag();
        CompoundTag helmet = new CompoundTag();
        helmet.putString("id", "minecraft:" + this.getMat().getKey().getKey());
        helmet.putByte("Count", (byte) 1);
        CompoundTag cmd = new CompoundTag();
        cmd.putInt("CustomModelData", this.getCustomModelData());
        helmet.put("tag", cmd);
        armorList.addAll(Arrays.asList(new CompoundTag(), new CompoundTag(), new CompoundTag(), helmet));
        spawnData.putString("id", "minecraft:armor_stand");
        spawnData.put("ArmorItems", armorList);
        spawnData.putByte("Marker", (byte) 1);
        spawnData.putByte("Invisible", (byte) 1);
        spawner.setNextSpawnData(server, pos, new SpawnData(spawnData, Optional.of(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 0), new InclusiveRange<>(0, 0)))));
        spawner.maxNearbyEntities = 0;
        spawner.requiredPlayerRange = 0;
        spawnerTE.setChanged();
        this.loc = loc;
    }

    @Override
    public CustomBlock clone() {
        try {
            return (CustomBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            //This should not happen, since we are Cloneable
            throw new AssertionError("Clone not supported", e);
        }
    }

    public NamespacedKey getName() {
        return name;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public Material getMat() {
        return mat;
    }

    public ItemStack[] getDrop() {
        return drop;
    }

    public int getXp() {
        return xp;
    }

    public Location getLoc() {
        return loc;
    }

    public ItemStack getAsItem() {
        return item;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("cmd", customModelData);
        result.put("mat", mat.toString());
        result.put("drops", drop);
        result.put("xp", xp);
        result.put("loc", loc);
        return result;
    }

    public static ImmutableList<CustomBlock> getRegisteredBlocks() {
        return ImmutableList.copyOf(REGISTERED_BLOCKS);
    }
}