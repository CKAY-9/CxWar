package ca.ckay9;

import org.bukkit.plugin.java.JavaPlugin;

import ca.ckay9.Commands.RevealCommand;

public class CxWar extends JavaPlugin {
    @Override
    public void onEnable() {
        Config.initializeData();

        this.getServer().getPluginCommand("reveal").setExecutor(new RevealCommand(this));
    }
}