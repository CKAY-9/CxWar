package ca.ckay9.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ca.ckay9.Config;
import ca.ckay9.CxWar;

public class HiddenCompleter implements TabCompleter {
    private CxWar cx_war;

    public HiddenCompleter(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player)sender;
        HiddenPlayer hidden_player = cx_war.hidden.getHiddenPlayers().get(player.getUniqueId());
        if (hidden_player == null) {
            hidden_player = new HiddenPlayer(0, Config.data.getInt("hidden.timer", 360));
            cx_war.hidden.addToHiddenPlayers(player.getUniqueId(), hidden_player);
        }
        
        ArrayList<String> options = new ArrayList<>();
        switch (args.length) {
            case 1:
                if (hidden_player.type == 0) {
                    options.add("start");
                } else {
                    options.add("end");
                }
                break;
            case 2:
                if (hidden_player.type == 0) {
                    options.add("diamond");
                    options.add("netherite");
                }
                break;
            default:
                break;
        }
        
        return options;
    }
    
}
