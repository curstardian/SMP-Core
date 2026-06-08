package io.main.smp.listener;

import io.main.smp.Msg;
import io.main.smp.Smp;
import io.main.smp.manager.CombatManager;
import io.main.smp.manager.LangManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private static final long CHAT_COOLDOWN_MS = 2_000L;

    private final Smp plugin;
    private final CombatManager combatManager;
    private final LangManager lang;

    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastMessage = new ConcurrentHashMap<>();
    private final Set<UUID> firstMessageExempt = ConcurrentHashMap.newKeySet();

    public ChatListener(Smp plugin) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
        this.lang = plugin.getLangManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        firstMessageExempt.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        firstMessageExempt.remove(id);
        lastMessageTime.remove(id);
        lastMessage.remove(id);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        String text = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (combatManager.isInCombat(player)) {
            event.setCancelled(true);
            player.sendMessage(Component.text(lang.get(Msg.CHAT_COMBAT), NamedTextColor.RED));
            return;
        }

        List<String> blocked = plugin.getConfig().getStringList("blocked-messages");
        for (String b : blocked) {
            if (text.equalsIgnoreCase(b)) {
                event.setCancelled(true);
                return;
            }
        }

        boolean exempt = firstMessageExempt.remove(id);

        if (!exempt) {
            long now = System.currentTimeMillis();
            Long last = lastMessageTime.get(id);
            if (last != null && now - last < CHAT_COOLDOWN_MS) {
                event.setCancelled(true);
                long remainingMs = CHAT_COOLDOWN_MS - (now - last);
                player.sendMessage(Component.text(
                        lang.get(Msg.CHAT_COOLDOWN, "seconds", String.format("%.1f", remainingMs / 1000.0)),
                        NamedTextColor.RED));
                return;
            }
        }

        String prev = lastMessage.get(id);
        if (text.equals(prev)) {
            event.setCancelled(true);
            player.sendMessage(Component.text(lang.get(Msg.CHAT_DUPLICATE), NamedTextColor.RED));
            return;
        }

        lastMessageTime.put(id, System.currentTimeMillis());
        lastMessage.put(id, text);
    }
}
