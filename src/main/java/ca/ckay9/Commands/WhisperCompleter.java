package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class WhisperCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> options = new ArrayList<>();
        switch (args.length) {
            case 1:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    options.add(p.getName());
                }
                break;
            case 2:
                options.add("message");
                break;
            default:
                break;
        }

        return options;
    }
}
