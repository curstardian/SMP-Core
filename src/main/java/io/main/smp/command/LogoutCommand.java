package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogoutCommand implements CommandExecutor {

    private final LangManager lang;

    public LogoutCommand(LangManager lang) {
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.get(Msg.PLAYER_ONLY));
            return true;
        }
        player.kick(Component.text(lang.get(Msg.LOGOUT_KICK)));
        return true;
    }
}
