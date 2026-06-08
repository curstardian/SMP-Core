package io.main.smp.manager;

import io.main.smp.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    private static final long TIMEOUT_TICKS = 20L * 60;

    private final JavaPlugin plugin;
    private final LangManager lang;
    private final Map<UUID, UUID> requests = new HashMap<>();
    private final Map<UUID, BukkitTask> timeouts = new HashMap<>();

    public TpaManager(JavaPlugin plugin, LangManager lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    public void sendRequest(Player from, Player to) {
        UUID fromId = from.getUniqueId();
        UUID toId = to.getUniqueId();
        String fromName = from.getName();
        cancelRequest(fromId);

        requests.put(fromId, toId);

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (requests.remove(fromId) != null) {
                timeouts.remove(fromId);
                Player f = plugin.getServer().getPlayer(fromId);
                if (f != null) f.sendMessage(Component.text(lang.get(Msg.TPA_EXPIRED_SENDER), NamedTextColor.RED));
                Player t = plugin.getServer().getPlayer(toId);
                if (t != null) t.sendMessage(Component.text(lang.get(Msg.TPA_EXPIRED_TARGET, "name", fromName), NamedTextColor.GRAY));
            }
        }, TIMEOUT_TICKS);
        timeouts.put(fromId, task);

        from.sendMessage(Component.text(lang.get(Msg.TPA_SENT, "name", to.getName()), NamedTextColor.YELLOW));
        to.sendMessage(Component.text(lang.get(Msg.TPA_RECEIVED, "name", from.getName()), NamedTextColor.YELLOW));
    }

    private UUID findRequesterFor(UUID targetId) {
        for (Map.Entry<UUID, UUID> e : requests.entrySet()) {
            if (e.getValue().equals(targetId)) return e.getKey();
        }
        return null;
    }

    public Player acceptRequest(Player target) {
        UUID requesterId = findRequesterFor(target.getUniqueId());
        if (requesterId == null) {
            target.sendMessage(Component.text(lang.get(Msg.TPA_NO_REQUEST), NamedTextColor.RED));
            return null;
        }
        cancelRequest(requesterId);
        Player requester = plugin.getServer().getPlayer(requesterId);
        if (requester == null) {
            target.sendMessage(Component.text(lang.get(Msg.TPA_OFFLINE), NamedTextColor.RED));
            return null;
        }
        return requester;
    }

    public void denyRequest(Player target) {
        UUID requesterId = findRequesterFor(target.getUniqueId());
        if (requesterId == null) {
            target.sendMessage(Component.text(lang.get(Msg.TPA_NO_REQUEST), NamedTextColor.RED));
            return;
        }
        cancelRequest(requesterId);
        target.sendMessage(Component.text(lang.get(Msg.TPA_DENY_SELF), NamedTextColor.RED));
        Player requester = plugin.getServer().getPlayer(requesterId);
        if (requester != null) {
            requester.sendMessage(Component.text(lang.get(Msg.TPA_DENY_OTHER, "name", target.getName()), NamedTextColor.RED));
        }
    }

    private void cancelRequest(UUID requesterId) {
        requests.remove(requesterId);
        BukkitTask t = timeouts.remove(requesterId);
        if (t != null) t.cancel();
    }
}
