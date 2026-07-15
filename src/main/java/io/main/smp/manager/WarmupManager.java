package io.main.smp.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks time since last combat hit per player.
 * Commands are gated until 15 s of no combat damage has passed.
 * The timer starts when the player enters combat and resets on each hit received.
 */
public class WarmupManager {

    static final long WARMUP_MS = 15_000L;

    private final Map<UUID, Long> lastHitTime = new HashMap<>();

    public void onCombatEnter(Player player) {
        lastHitTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void onCombatDamage(Player player) {
        UUID id = player.getUniqueId();
        if (lastHitTime.containsKey(id)) {
            lastHitTime.put(id, System.currentTimeMillis());
        }
    }

    public void onCombatLeave(UUID id) {
        lastHitTime.remove(id);
    }

    public boolean canUseCommand(Player player) {
        Long t = lastHitTime.get(player.getUniqueId());
        if (t == null) return true;
        return System.currentTimeMillis() - t >= WARMUP_MS;
    }

    public long remainingMs(Player player) {
        Long t = lastHitTime.get(player.getUniqueId());
        if (t == null) return 0;
        return Math.max(0L, WARMUP_MS - (System.currentTimeMillis() - t));
    }
}
