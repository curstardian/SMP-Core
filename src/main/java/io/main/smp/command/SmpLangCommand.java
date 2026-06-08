package io.main.smp.command;

import io.main.smp.Msg;
import io.main.smp.manager.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmpLangCommand implements CommandExecutor {

    private final LangManager lang;

    public SmpLangCommand(LangManager lang) {
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Console is always allowed; players need op
        if (sender instanceof Player player && !player.isOp()) {
            player.sendMessage(Component.text(lang.get(Msg.LANG_NO_PERMISSION), NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(lang.get(Msg.LANG_USAGE));
            return true;
        }
        String target = args[0].toLowerCase();
        if (!target.equals("kr") && !target.equals("en")) {
            sender.sendMessage(lang.get(Msg.LANG_INVALID));
            return true;
        }
        lang.setLanguage(target);
        sender.sendMessage(lang.get(Msg.LANG_SET, "lang", target));
        return true;
    }
}
