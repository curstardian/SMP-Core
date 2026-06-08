package io.main.smp.manager;

import io.main.smp.Msg;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private static final long COMBAT_TICKS      = 20L * 60 * 30;
    private static final long COMBAT_DURATION_MS = 30L * 60 * 1000;

    private final JavaPlugin plugin;
    private final LangManager lang;
    private final WarmupManager warmup;

    private final Map<UUID, UUID>       opponents    = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> timers       = new HashMap<>();
    private final Set<UUID>             cancelRequests = new HashSet<>();
    private final Map<UUID, BossBar>    bossBars     = new HashMap<>();
    private final Map<UUID, Long>       combatStart  = new HashMap<>();

    private BukkitTask tickTask;

    public CombatManager(JavaPlugin plugin, LangManager lang, WarmupManager warmup) {
        this.plugin = plugin;
        this.lang   = lang;
        this.warmup = warmup;
        tickTask = plugin.getServer().getScheduler()
                .runTaskTimer(plugin, this::tick, 20L, 20L);
    }

    // ── per-second update ────────────────────────────────────────────────────

    private void tick() {
        long now = System.currentTimeMillis();
        for (UUID id : opponents.keySet()) {
            Player p = plugin.getServer().getPlayer(id);
            if (p == null) continue;

            // Action bar
            long remainMs = warmup.remainingMs(p);
            Component bar;
            if (remainMs > 0) {
                long secs = (remainMs + 999) / 1000;
                bar = Component.text(lang.get(Msg.ACTION_BAR_CMD_LOCK, "seconds", String.valueOf(secs)), NamedTextColor.RED);
            } else {
                bar = Component.text(lang.get(Msg.ACTION_BAR_COMBAT), NamedTextColor.RED);
            }
            p.sendActionBar(bar);

            // Boss bar
            BossBar bossBar = bossBars.get(id);
            Long start = combatStart.get(id);
            if (bossBar != null && start != null) {
                long elapsed    = now - start;
                long remaining  = Math.max(0L, COMBAT_DURATION_MS - elapsed);
                float progress  = (float) remaining / COMBAT_DURATION_MS;
                long totalSecs  = remaining / 1000;
                long mins       = totalSecs / 60;
                long secs       = totalSecs % 60;
                bossBar.name(Component.text(lang.get(Msg.BOSSBAR_TITLE) + " | " + String.format("%02d:%02d", mins, secs), NamedTextColor.RED));
                bossBar.progress(Math.max(0f, Math.min(1f, progress)));
            }
        }
    }

    // ── enter / leave helpers ────────────────────────────────────────────────

    private void setupCombatant(Player player) {
        UUID id = player.getUniqueId();
        combatStart.put(id, System.currentTimeMillis());

        BossBar bar = BossBar.bossBar(
                Component.text(lang.get(Msg.BOSSBAR_TITLE), NamedTextColor.RED),
                1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        bossBars.put(id, bar);
        player.showBossBar(bar);

        warmup.onCombatEnter(player);

        player.sendMessage(Component.text(lang.get(Msg.COMBAT_ENTER), NamedTextColor.RED));
        player.sendMessage(Component.text(lang.get(Msg.COMBAT_CHAT_INFO), NamedTextColor.YELLOW));
    }

    private void cleanupCombatant(UUID id) {
        combatStart.remove(id);
        BossBar bar = bossBars.remove(id);
        Player p = plugin.getServer().getPlayer(id);
        if (bar != null && p != null) p.hideBossBar(bar);
        warmup.onCombatLeave(id);
    }

    // ── public API ───────────────────────────────────────────────────────────

    public void enterCombat(Player attacker, Player victim) {
        UUID aId = attacker.getUniqueId();
        UUID vId = victim.getUniqueId();

        UUID aPrev = opponents.get(aId);
        if (aPrev != null && !aPrev.equals(vId)) removeCombat(aPrev);
        UUID vPrev = opponents.get(vId);
        if (vPrev != null && !vPrev.equals(aId)) removeCombat(vPrev);

        boolean aNew = !opponents.containsKey(aId);
        boolean vNew = !opponents.containsKey(vId);

        cancelTimer(aId);
        cancelTimer(vId);

        opponents.put(aId, vId);
        opponents.put(vId, aId);
        cancelRequests.remove(aId);
        cancelRequests.remove(vId);

        timers.put(aId, plugin.getServer().getScheduler().runTaskLater(plugin, () -> expireCombat(aId), COMBAT_TICKS));
        timers.put(vId, plugin.getServer().getScheduler().runTaskLater(plugin, () -> expireCombat(vId), COMBAT_TICKS));

        if (aNew) setupCombatant(attacker);
        if (vNew) setupCombatant(victim);
    }

    public boolean isInCombat(Player p) {
        return opponents.containsKey(p.getUniqueId());
    }

    public void onDeath(Player player) {
        UUID id = player.getUniqueId();
        UUID opId = opponents.get(id);
        if (opId == null) return;
        clearBothSides(id, opId);
        cleanupCombatant(id);
        cleanupCombatant(opId);
        Player op = plugin.getServer().getPlayer(opId);
        if (op != null) op.sendMessage(Component.text(lang.get(Msg.COMBAT_DEATH_OTHER), NamedTextColor.GREEN));
    }

    public void onQuit(Player player) {
        UUID id = player.getUniqueId();
        UUID opId = opponents.get(id);
        if (opId == null) return;
        clearBothSides(id, opId);
        cleanupCombatant(id);
        cleanupCombatant(opId);
        Player op = plugin.getServer().getPlayer(opId);
        if (op != null) op.sendMessage(Component.text(lang.get(Msg.COMBAT_QUIT_OTHER), NamedTextColor.GREEN));
    }

    public void requestCancel(Player player) {
        UUID id = player.getUniqueId();
        if (!opponents.containsKey(id)) {
            player.sendMessage(Component.text(lang.get(Msg.COMBAT_NOT_IN), NamedTextColor.RED));
            return;
        }
        UUID opId = opponents.get(id);
        cancelRequests.add(id);

        if (cancelRequests.contains(opId)) {
            clearBothSides(id, opId);
            cleanupCombatant(id);
            cleanupCombatant(opId);
            player.sendMessage(Component.text(lang.get(Msg.CANCEL_DONE), NamedTextColor.GREEN));
            Player op = plugin.getServer().getPlayer(opId);
            if (op != null) op.sendMessage(Component.text(lang.get(Msg.CANCEL_DONE), NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text(lang.get(Msg.CANCEL_WAITING), NamedTextColor.YELLOW));
            Player op = plugin.getServer().getPlayer(opId);
            if (op != null) {
                Component confirm = Component.text()
                        .append(Component.text(lang.get(Msg.CANCEL_REQUEST_HEADER, "name", player.getName()) + "\n", NamedTextColor.YELLOW))
                        .append(Component.text(lang.get(Msg.CANCEL_CONFIRM_QUESTION) + "  ", NamedTextColor.YELLOW))
                        .append(Component.text(lang.get(Msg.CANCEL_YES_BTN), NamedTextColor.GREEN, TextDecoration.BOLD)
                                .clickEvent(ClickEvent.runCommand("/pvpcancel")))
                        .append(Component.text("  "))
                        .append(Component.text(lang.get(Msg.CANCEL_NO_BTN), NamedTextColor.RED, TextDecoration.BOLD)
                                .clickEvent(ClickEvent.runCommand("/pvpdeny")))
                        .build();
                op.sendMessage(confirm);
            }
        }
    }

    public void denyCancel(Player denier) {
        UUID denierId = denier.getUniqueId();
        if (!opponents.containsKey(denierId)) {
            denier.sendMessage(Component.text(lang.get(Msg.COMBAT_NOT_IN), NamedTextColor.RED));
            return;
        }
        UUID requesterId = opponents.get(denierId);
        if (!cancelRequests.contains(requesterId)) {
            denier.sendMessage(Component.text(lang.get(Msg.CANCEL_NO_REQUEST), NamedTextColor.RED));
            return;
        }
        cancelRequests.remove(requesterId);
        denier.sendMessage(Component.text(lang.get(Msg.CANCEL_DENIED_SELF), NamedTextColor.RED));
        Player requester = plugin.getServer().getPlayer(requesterId);
        if (requester != null)
            requester.sendMessage(Component.text(lang.get(Msg.CANCEL_DENIED_OTHER, "name", denier.getName()), NamedTextColor.RED));
    }

    public void shutdown() {
        if (tickTask != null) tickTask.cancel();
        timers.values().forEach(BukkitTask::cancel);
        timers.clear();
        opponents.clear();
        cancelRequests.clear();
        bossBars.clear();
        combatStart.clear();
    }

    // ── internal helpers ─────────────────────────────────────────────────────

    private void expireCombat(UUID id) {
        if (!opponents.containsKey(id)) return;
        UUID opId = opponents.remove(id);
        timers.remove(id);
        cancelRequests.remove(id);
        cleanupCombatant(id);
        Player p = plugin.getServer().getPlayer(id);
        if (p != null) p.sendMessage(Component.text(lang.get(Msg.COMBAT_EXPIRE), NamedTextColor.GREEN));

        if (opId != null) {
            cancelTimer(opId);
            opponents.remove(opId);
            cancelRequests.remove(opId);
            cleanupCombatant(opId);
            Player op = plugin.getServer().getPlayer(opId);
            if (op != null) op.sendMessage(Component.text(lang.get(Msg.COMBAT_EXPIRE), NamedTextColor.GREEN));
        }
    }

    private void removeCombat(UUID id) {
        cancelTimer(id);
        opponents.remove(id);
        cancelRequests.remove(id);
        cleanupCombatant(id);
        Player p = plugin.getServer().getPlayer(id);
        if (p != null) p.sendMessage(Component.text(lang.get(Msg.COMBAT_EXPIRE), NamedTextColor.GREEN));
    }

    private void clearBothSides(UUID id, UUID opId) {
        cancelTimer(id);
        opponents.remove(id);
        cancelRequests.remove(id);
        cancelTimer(opId);
        opponents.remove(opId);
        cancelRequests.remove(opId);
    }

    private void cancelTimer(UUID id) {
        BukkitTask t = timers.remove(id);
        if (t != null) t.cancel();
    }
}
