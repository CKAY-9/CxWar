package ca.ckay9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ca.ckay9.Commands.CxWarCommand;
import ca.ckay9.Commands.GroupCommand;
import ca.ckay9.Commands.GroupCompleter;
import ca.ckay9.Commands.HiddenCommand;
import ca.ckay9.Commands.HiddenCompleter;
import ca.ckay9.Commands.KillstreakCommand;
import ca.ckay9.Commands.RevealCommand;
import ca.ckay9.Commands.TPACommand;
import ca.ckay9.Commands.TPACompleter;
import ca.ckay9.Commands.TPRCommand;
import ca.ckay9.Commands.WhisperCommand;
import ca.ckay9.Commands.WhisperCompleter;
import ca.ckay9.Listeners.PlayerDamage;
import ca.ckay9.Listeners.PlayerJoin;
import ca.ckay9.Listeners.PlayerKill;
import ca.ckay9.Listeners.PlayerLeave;

public class CxWar extends JavaPlugin {
    public HiddenCommand hidden;
    public ArrayList<Group> groups;
    public Killstreaks killstreaks;
    public Teleports teleports;
    public HashMap<UUID, Integer> combat_logs;
    public HashSet<UUID> die_on_leave;

    @Override
    public void onEnable() {
        Storage.initializeData();

        // Combat Logs
        if (Storage.config.getBoolean("combat_logging.enabled", true)) {
            this.combat_logs = new HashMap<>();
            if (Storage.config.getBoolean("combat_logging.die_if_leave", true)) {
                this.die_on_leave = new HashSet<>();
                ConfigurationSection dol = Storage.data.getConfigurationSection("dol");
                if (dol != null) {
                    for (String key : dol.getKeys(false)) {
                        if (dol.getBoolean(key)) {
                            die_on_leave.add(UUID.fromString(key));
                        }
                    }
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                combat_logs.put(player.getUniqueId(), 5);
            }

            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    for (Entry<UUID, Integer> entry : combat_logs.entrySet()) {
                        if (entry.getValue() > 0) {
                            combat_logs.put(entry.getKey(), entry.getValue() - 1);
                        }
                    }
                }
            }, 0, 20L);
        }

        // Listeners
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerKill(this), this);


        // Teleports
        if (Storage.config.getBoolean("tp.enabled", true)) {
            this.teleports = new Teleports(this);
        }
        this.getServer().getPluginCommand("tpr").setExecutor(new TPRCommand(this));
        this.getServer().getPluginCommand("tpa").setExecutor(new TPACommand(this));
        this.getServer().getPluginCommand("tpa").setTabCompleter(new TPACompleter(this));

        // Reveal
        this.getServer().getPluginCommand("reveal").setExecutor(new RevealCommand(this));

        // Hidden
        this.hidden = new HiddenCommand(this);
        this.getServer().getPluginCommand("hidden").setExecutor(this.hidden);
        this.getServer().getPluginCommand("hidden").setTabCompleter(new HiddenCompleter(this));

        // Group
        if (Storage.config.getBoolean("groups.enabled", true)) {
            this.groups = Group.loadAllGroups();
        }
        this.getServer().getPluginCommand("group").setExecutor(new GroupCommand(this));
        this.getServer().getPluginCommand("group").setTabCompleter(new GroupCompleter(this));

        // Killstreaks
        if (Storage.config.getBoolean("killstreaks.enabled", true)) {
            this.killstreaks = new Killstreaks(this);
        }
        this.getServer().getPluginCommand("killstreak").setExecutor(new KillstreakCommand(this));

        // Whispers
        this.getServer().getPluginCommand("whisper").setExecutor(new WhisperCommand());
        this.getServer().getPluginCommand("whisper").setTabCompleter(new WhisperCompleter());

        // Misc
        this.getServer().getPluginCommand("cxwar").setExecutor(new CxWarCommand());
    }

    @Override
    public void onDisable() {
        if (Storage.config.getBoolean("combat_logging.enabled", true)) {
            this.combat_logs.clear();
        }
        if (Storage.config.getBoolean("combat_logging.die_if_leave", true)) {
            this.die_on_leave.clear();
        }
    }
}