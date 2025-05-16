package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Group;
import ca.ckay9.Utils;

public class GroupCompleter implements TabCompleter {
    private CxWar cx_war;

    public GroupCompleter(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        ArrayList<String> options = new ArrayList<>();
        Group current_group = Group.getPlayerGroup(player.getUniqueId(), this.cx_war.groups);

        switch (args.length) {
            case 1:
                if (current_group == null) {
                    options.add("create");
                    options.add("join");
                } else {
                    if (current_group.creator.equals(player.getUniqueId())) {
                        options.add("delete");
                        options.add("invite");
                    } else {
                        options.add("leave");
                    }
                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "create":
                        options.add("name");
                        break;
                    case "join":
                        options.addAll(Group.getGroupsInvitedTo(player.getUniqueId(), this.cx_war.groups));
                        break;
                    case "invite":
                        for (Player temp_player : Bukkit.getOnlinePlayers()) {
                            options.add(temp_player.getName());
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return options;
    }

}
