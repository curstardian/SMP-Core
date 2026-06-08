package io.main.smp.listener;

import io.main.smp.Smp;
import io.main.smp.manager.CombatManager;
import io.main.smp.manager.WarmupManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    private final CombatManager combatManager;
    private final WarmupManager warmupManager;

    public CombatListener(Smp plugin) {
        this.combatManager = plugin.getCombatManager();
        this.warmupManager = plugin.getWarmupManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = resolveAttacker(event);
        if (attacker == null || attacker.equals(victim)) return;

        combatManager.enterCombat(attacker, victim);
        warmupManager.onCombatDamage(victim);
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        // Direct player hit
        if (damager instanceof Player p) return p;

        // Any projectile shot by a player (arrow, trident, potion, snowball, egg, etc.)
        // Skeleton/stray arrows are excluded automatically — their shooter is not a Player
        if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player p) return p;
            return null;
        }

        // TNT lit by a player
        if (damager instanceof TNTPrimed tnt) {
            if (tnt.getSource() instanceof Player p) return p;
            return null;
        }

        // EnderCrystal / TNT Minecart: Paper attributes the causing player via DamageSource
        Entity causing = event.getDamageSource().getCausingEntity();
        if (causing instanceof Player p) return p;

        return null;
    }
}
