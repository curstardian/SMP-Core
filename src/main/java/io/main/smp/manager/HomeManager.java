package io.main.smp.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private static final int MAX_HOMES = 3;

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public HomeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String uuidStr : cfg.getKeys(false)) {
            UUID uuid;
            try { uuid = UUID.fromString(uuidStr); } catch (IllegalArgumentException e) { continue; }
            var section = cfg.getConfigurationSection(uuidStr);
            if (section == null) continue;
            Map<String, Location> map = new HashMap<>();
            for (String name : section.getKeys(false)) {
                String p = uuidStr + "." + name + ".";
                String worldName = cfg.getString(p + "world");
                if (worldName == null) continue;
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                map.put(name, new Location(world,
                        cfg.getDouble(p + "x"), cfg.getDouble(p + "y"), cfg.getDouble(p + "z"),
                        (float) cfg.getDouble(p + "yaw"), (float) cfg.getDouble(p + "pitch")));
            }
            homes.put(uuid, map);
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (var entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (var homeEntry : entry.getValue().entrySet()) {
                String p = uuidStr + "." + homeEntry.getKey() + ".";
                Location loc = homeEntry.getValue();
                cfg.set(p + "world", loc.getWorld().getName());
                cfg.set(p + "x", loc.getX());
                cfg.set(p + "y", loc.getY());
                cfg.set(p + "z", loc.getZ());
                cfg.set(p + "yaw", (double) loc.getYaw());
                cfg.set(p + "pitch", (double) loc.getPitch());
            }
        }
        try {
            plugin.getDataFolder().mkdirs();
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("homes.yml 저장 실패: " + e.getMessage());
        }
    }

    public boolean canSetHome(Player player, String name) {
        Map<String, Location> map = homes.get(player.getUniqueId());
        if (map == null) return true;
        return map.containsKey(name) || map.size() < MAX_HOMES;
    }

    // Returns false if at max homes and name is new
    public boolean setHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Map<String, Location> map = homes.computeIfAbsent(uuid, k -> new HashMap<>());
        if (!map.containsKey(name) && map.size() >= MAX_HOMES) return false;
        map.put(name, player.getLocation().clone());
        save();
        return true;
    }

    public Location getHome(Player player, String name) {
        Map<String, Location> map = homes.get(player.getUniqueId());
        return map == null ? null : map.get(name);
    }

    public Map<String, Location> getHomes(Player player) {
        return homes.getOrDefault(player.getUniqueId(), Collections.emptyMap());
    }

    // Returns false if the home name does not exist
    public boolean deleteHome(Player player, String name) {
        Map<String, Location> map = homes.get(player.getUniqueId());
        if (map == null || !map.containsKey(name)) return false;
        map.remove(name);
        if (map.isEmpty()) homes.remove(player.getUniqueId());
        save();
        return true;
    }
}
