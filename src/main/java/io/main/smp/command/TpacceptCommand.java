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

public class TpacceptCommand implements CommandExecutor {

    private final TpaManager tpaManager;
    private final LangManager lang;

    public TpacceptCommand(TpaManager tpaManager, LangManager lang) {
        this.tpaManager = tpaManager;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player target)) {
            sender.sendMessage(lang.get(Msg.PLAYER_ONLY));
            return true;
        }
        Player requester = tpaManager.acceptRequest(target);
        if (requester == null) return true;

        requester.teleport(target.getLocation());
        target.sendMessage(Component.text(lang.get(Msg.TPA_ACCEPT_TARGET, "name", requester.getName()), NamedTextColor.GREEN));
        requester.sendMessage(Component.text(lang.get(Msg.TPA_ACCEPT_REQUESTER, "name", target.getName()), NamedTextColor.GREEN));
        target.sendMessage(Component.text(lang.get(Msg.TPA_ACCEPT_TARGET2, "name", requester.getName()), NamedTextColor.GREEN));
        return true;
    }
}
