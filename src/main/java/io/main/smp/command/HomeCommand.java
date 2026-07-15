package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.HomeManager;
import io.main.smp.manager.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final LangManager lang;

    public HomeCommand(HomeManager homeManager, LangManager lang) {
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
            Map<String, Location> homes = homeManager.getHomes(player);
            if (homes.isEmpty()) {
                player.sendMessage(Component.text(lang.get(Msg.HOME_NONE), NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text(lang.get(Msg.HOME_LIST, "list", String.join(", ", homes.keySet())), NamedTextColor.YELLOW));
            }
            return true;
        }
        String name = args[0];
        Location home = homeManager.getHome(player, name);
        if (home == null) {
            player.sendMessage(Component.text(lang.get(Msg.HOME_NOT_FOUND, "name", name), NamedTextColor.RED));
            return true;
        }
        player.teleport(home);
        player.sendMessage(Component.text(lang.get(Msg.HOME_TELEPORT, "name", name), NamedTextColor.GREEN));
        return true;
    }
}
