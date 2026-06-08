package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.HomeManager;
import io.main.smp.manager.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelhomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final LangManager lang;

    public DelhomeCommand(HomeManager homeManager, LangManager lang) {
        this.homeManager = homeManager;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.get(Msg.PLAYER_ONLY));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(Component.text(lang.get(Msg.HOME_USAGE_DEL), NamedTextColor.RED));
            return true;
        }
        String name = args[0];
        if (homeManager.deleteHome(player, name)) {
            player.sendMessage(Component.text(lang.get(Msg.HOME_DEL, "name", name), NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text(lang.get(Msg.HOME_NOT_FOUND, "name", name), NamedTextColor.RED));
        }
        return true;
    }
}
