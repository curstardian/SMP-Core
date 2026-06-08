package io.main.smp.command;

import io.main.smp.manager.CombatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvpCancelCommand implements CommandExecutor {

    private final CombatManager combatManager;

    public PvpCancelCommand(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용 가능합니다.");
            return true;
        }
        combatManager.requestCancel(player);
        return true;
    }
}
