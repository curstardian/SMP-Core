package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.LangManager;
import io.main.smp.manager.TpaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpdenyCommand implements CommandExecutor {

    private final TpaManager tpaManager;
    private final LangManager lang;

    public TpdenyCommand(TpaManager tpaManager, LangManager lang) {
        this.tpaManager = tpaManager;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.get(Msg.PLAYER_ONLY));
            return true;
        }
        tpaManager.denyRequest(player);
        return true;
    }
}
