package ca.ckay9;

import org.bukkit.plugin.java.JavaPlugin;

import ca.ckay9.Commands.HiddenCommand;
import ca.ckay9.Commands.HiddenCompleter;
import ca.ckay9.Commands.RevealCommand;

public class CxWar extends JavaPlugin {
    public HiddenCommand hidden;

    @Override
    public void onEnable() {
        Config.initializeData();

        this.getServer().getPluginCommand("reveal").setExecutor(new RevealCommand(this));
        
        this.hidden = new HiddenCommand(this);
        this.getServer().getPluginCommand("hidden").setExecutor(this.hidden);
        this.getServer().getPluginCommand("hidden").setTabCompleter(new HiddenCompleter(this));
    }
}