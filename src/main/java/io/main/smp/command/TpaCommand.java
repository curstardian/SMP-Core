package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.LangManager;
import io.main.smp.manager.TpaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TpaCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final TpaManager tpaManager;
    private final LangManager lang;

    public TpaCommand(JavaPlugin plugin, TpaManager tpaManager, LangManager lang) {
        this.plugin = plugin;
        this.tpaManager = tpaManager;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.get(Msg.PLAYER_ONLY));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Component.text(lang.get(Msg.TPA_USAGE), NamedTextColor.RED));
            return true;
        }
        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(Component.text(lang.get(Msg.TPA_NOT_FOUND), NamedTextColor.RED));
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage(Component.text(lang.get(Msg.TPA_SELF), NamedTextColor.RED));
            return true;
        }
        tpaManager.sendRequest(player, target);
        return true;
    }
}
