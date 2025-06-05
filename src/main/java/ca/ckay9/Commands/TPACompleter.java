package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;

public class TPACompleter implements TabCompleter {
    private CxWar cx_war;

    public TPACompleter(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> options = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return options;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String name = args[0].toLowerCase().strip();
            for (Entry<UUID, UUID> entry : this.cx_war.teleports.requests.entrySet()) {
                if (!entry.getValue().equals(player.getUniqueId())) {
                    continue;
                }

                Player requesting_player = Bukkit.getPlayer(entry.getKey());
                if (requesting_player == null) {
                    continue;
                }
                String requesting_name = requesting_player.getName();
                if (requesting_name.toLowerCase().contains(name)) {
                    options.add(requesting_name);
                }
            }
        }

        return options;
    }

}
