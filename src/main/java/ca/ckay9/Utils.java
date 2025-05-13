package ca.ckay9;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class Utils {
    public static String formatText(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("CxWar");
    }
}
