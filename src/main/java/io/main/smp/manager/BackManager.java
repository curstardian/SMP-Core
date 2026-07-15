package io.main.smp.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackManager {

    private static final long BACK_WINDOW_MS = 60_000L;

    private record DeathRecord(Location location, long time) {}

    private final Map<UUID, DeathRecord> deaths = new HashMap<>();

    public void recordDeath(Player player) {
        deaths.put(player.getUniqueId(), new DeathRecord(player.getLocation().clone(), System.currentTimeMillis()));
    }

    public Location getBackLocation(Player player) {
        UUID id = player.getUniqueId();
        DeathRecord r = deaths.get(id);
        if (r == null) return null;
        if (System.currentTimeMillis() - r.time() > BACK_WINDOW_MS) {
            deaths.remove(id);
            return null;
        }
        return r.location();
    }
}
