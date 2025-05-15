package ca.ckay9;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

import ca.ckay9.Commands.GroupCommand;
import ca.ckay9.Commands.GroupCompleter;
import ca.ckay9.Commands.HiddenCommand;
import ca.ckay9.Commands.HiddenCompleter;
import ca.ckay9.Commands.RevealCommand;

public class CxWar extends JavaPlugin {
    public HiddenCommand hidden;
    public SocketServer socket_server;
    public ArrayList<Group> groups;

    @Override
    public void onEnable() {
        Config.initializeData();

        // Reveal
        this.getServer().getPluginCommand("reveal").setExecutor(new RevealCommand(this));

        // Hidden
        this.hidden = new HiddenCommand(this);
        this.getServer().getPluginCommand("hidden").setExecutor(this.hidden);
        this.getServer().getPluginCommand("hidden").setTabCompleter(new HiddenCompleter(this));

        // Group
        this.groups = Group.loadAllGroups();
        this.getServer().getPluginCommand("group").setExecutor(new GroupCommand(this));
        this.getServer().getPluginCommand("group").setTabCompleter(new GroupCompleter(this));

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