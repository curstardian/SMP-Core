package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.BackManager;
import io.main.smp.manager.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    private final BackManager backManager;
    private final LangManager lang;

    public BackCommand(BackManager backManager, LangManager lang) {
        this.backManager = backManager;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.get(Msg.PLAYER_ONLY));
            return true;
        }
        Location back = backManager.getBackLocation(player);
        if (back == null) {
            player.sendMessage(Component.text(lang.get(Msg.BACK_NO_LOCATION), NamedTextColor.RED));
            return true;
        }
        player.teleport(back);
        player.sendMessage(Component.text(lang.get(Msg.BACK_SUCCESS), NamedTextColor.GREEN));
        return true;
    }
}
