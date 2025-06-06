package ca.ckay9;

import java.net.UnknownHostException;
import java.util.ArrayList;

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
    public SocketServer socket_server;
    public ArrayList<Group> groups;
    public Killstreaks killstreaks;
    public Teleports teleports;

    @Override
    public void onEnable() {
        Storage.initializeData();

        // Teleports
        this.teleports = new Teleports(this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(this), this);
        this.getServer().getPluginCommand("tpr").setExecutor(new TPRCommand(this));
        this.getServer().getPluginCommand("tpa").setExecutor(new TPACommand(this));
        this.getServer().getPluginCommand("tpa").setTabCompleter(new TPACompleter(this));

        // Reveal
        this.getServer().getPluginCommand("reveal").setExecutor(new RevealCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);

        // Hidden
        this.hidden = new HiddenCommand(this);
        this.getServer().getPluginCommand("hidden").setExecutor(this.hidden);
        this.getServer().getPluginCommand("hidden").setTabCompleter(new HiddenCompleter(this));

        // Group
        this.groups = Group.loadAllGroups();
        this.getServer().getPluginCommand("group").setExecutor(new GroupCommand(this));
        this.getServer().getPluginCommand("group").setTabCompleter(new GroupCompleter(this));
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);

        // Killstreaks
        this.killstreaks = new Killstreaks(this);
        this.getServer().getPluginManager().registerEvents(new PlayerKill(this), this);
        this.getServer().getPluginCommand("killstreak").setExecutor(new KillstreakCommand(this));

        // Whispers
        this.getServer().getPluginCommand("whisper").setExecutor(new WhisperCommand());
        this.getServer().getPluginCommand("whisper").setTabCompleter(new WhisperCompleter());

        // Misc
        this.getServer().getPluginCommand("cxwar").setExecutor(new CxWarCommand());

        try {
            int port = 8887;
            this.socket_server = new SocketServer(port);
            this.socket_server.start();
            this.getLogger().info("Started socket server on port " + port);
        } catch (UnknownHostException exception) {
            this.getLogger().warning("Failed to start socket server: " + exception.getMessage());
        }
    }

    @Override
    public void onDisable() {
        try {
            if (this.socket_server != null) {
                this.socket_server.stop();
            }
        } catch (InterruptedException exception) {
            this.getLogger().warning("Failed to stop socket server: " + exception.getMessage());
        }
    }
}