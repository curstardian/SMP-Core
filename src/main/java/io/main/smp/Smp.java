package io.main.smp;

import io.main.smp.command.BackCommand;
import io.main.smp.command.DelhomeCommand;
import io.main.smp.command.HomeCommand;
import io.main.smp.command.LogoutCommand;
import io.main.smp.command.PvpCancelCommand;
import io.main.smp.command.PvpDenyCommand;
import io.main.smp.command.SethomeCommand;
import io.main.smp.command.SmpLangCommand;
import io.main.smp.command.TpacceptCommand;
import io.main.smp.command.TpaCommand;
import io.main.smp.command.TpdenyCommand;
import io.main.smp.listener.ChatListener;
import io.main.smp.listener.CombatListener;
import io.main.smp.listener.PlayerListener;
import io.main.smp.manager.BackManager;
import io.main.smp.manager.CombatManager;
import io.main.smp.manager.HomeManager;
import io.main.smp.manager.LangManager;
import io.main.smp.manager.TpaManager;
import io.main.smp.manager.WarmupManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Smp extends JavaPlugin {

    private LangManager langManager;
    private CombatManager combatManager;
    private TpaManager tpaManager;
    private HomeManager homeManager;
    private BackManager backManager;
    private WarmupManager warmupManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        langManager   = new LangManager(this);
        warmupManager = new WarmupManager();
        combatManager = new CombatManager(this, langManager, warmupManager);
        tpaManager    = new TpaManager(this, langManager);
        homeManager   = new HomeManager(this);
        backManager   = new BackManager();

        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getCommand("pvpcancel").setExecutor(new PvpCancelCommand(combatManager));
        getCommand("pvpdeny").setExecutor(new PvpDenyCommand(combatManager));
        getCommand("tpa").setExecutor(new TpaCommand(this, tpaManager, langManager));
        getCommand("tpaccept").setExecutor(new TpacceptCommand(tpaManager, langManager));
        getCommand("tpdeny").setExecutor(new TpdenyCommand(tpaManager, langManager));
        getCommand("sethome").setExecutor(new SethomeCommand(homeManager, langManager));
        getCommand("delhome").setExecutor(new DelhomeCommand(homeManager, langManager));
        getCommand("home").setExecutor(new HomeCommand(homeManager, langManager));
        getCommand("back").setExecutor(new BackCommand(backManager, langManager));
        getCommand("logout").setExecutor(new LogoutCommand(langManager));
        getCommand("smplang").setExecutor(new SmpLangCommand(langManager));
    }

    @Override
    public void onDisable() {
        combatManager.shutdown();
        homeManager.save();
    }

    public LangManager getLangManager() { return langManager; }
    public CombatManager getCombatManager() { return combatManager; }
    public TpaManager getTpaManager() { return tpaManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public BackManager getBackManager() { return backManager; }
    public WarmupManager getWarmupManager() { return warmupManager; }
}
