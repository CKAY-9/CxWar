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

    public static String combineStringArrayIntoSingle(String[] strings, int start_index) {
        StringBuilder builder = new StringBuilder();
        for (int index = start_index; index < strings.length; index++) {
            builder.append(strings[index].strip());
            if (index != strings.length - 1) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }
}
