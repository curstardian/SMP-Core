package io.main.smp.listener;

import io.main.smp.Msg;
import io.main.smp.Smp;
import io.main.smp.manager.BackManager;
import io.main.smp.manager.CombatManager;
import io.main.smp.manager.LangManager;
import io.main.smp.manager.WarmupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final CombatManager combatManager;
    private final BackManager backManager;
    private final WarmupManager warmupManager;
    private final LangManager lang;

    public PlayerListener(Smp plugin) {
        this.combatManager = plugin.getCombatManager();
        this.backManager   = plugin.getBackManager();
        this.warmupManager = plugin.getWarmupManager();
        this.lang          = plugin.getLangManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        backManager.recordDeath(player);
        combatManager.onDeath(player);
        warmupManager.onCombatLeave(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatManager.isInCombat(player)) {
            player.setHealth(0);
        }
        combatManager.onQuit(player);
        warmupManager.onCombatLeave(player.getUniqueId());
    }

    // Global combat command gate
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!combatManager.isInCombat(player)) return;
        if (warmupManager.canUseCommand(player)) return;

        event.setCancelled(true);
        long remainMs = warmupManager.remainingMs(player);
        long secs = (remainMs + 999) / 1000;
        player.sendMessage(Component.text(lang.get(Msg.CMD_BLOCKED, "seconds", String.valueOf(secs)), NamedTextColor.RED));
    }
}
