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

public class SethomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final LangManager lang;

    public SethomeCommand(HomeManager homeManager, LangManager lang) {
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
            player.sendMessage(Component.text(lang.get(Msg.HOME_USAGE_SET), NamedTextColor.RED));
            return true;
        }
        String name = args[0];
        if (!name.matches("[a-zA-Z0-9_가-힣]+")) {
            player.sendMessage(Component.text(lang.get(Msg.HOME_INVALID_NAME), NamedTextColor.RED));
            return true;
        }
        if (homeManager.setHome(player, name)) {
            player.sendMessage(Component.text(lang.get(Msg.HOME_SET, "name", name), NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text(lang.get(Msg.HOME_MAX), NamedTextColor.RED));
        }
        return true;
    }
}
