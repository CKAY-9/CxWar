package ca.ckay9.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Utils;

public class TPRCommand implements CommandExecutor {
    private CxWar cx_war;

    public TPRCommand(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length <= 0) {
            player.sendMessage(
                    Utils.formatText("&cInvalid tpa usage: /tpa [player]"));
            return false;
        }

        String target_name = args[0];
        Player target_player = Bukkit.getPlayerExact(target_name);
        if (target_player == null) {
            player.sendMessage(
                    Utils.formatText("&cUnable to find target player"));
            return false;
        }

        this.cx_war.teleports.requests.put(player.getUniqueId(), target_player.getUniqueId());
        target_player.sendMessage(Utils.formatText(
                "&a&l" + player.getName() + "&r&a has sent a teleport request. Type &a&l/tpa " + player.getName()));
        player.sendMessage(Utils.formatText("&aSent a teleport request to &a&l" + target_player.getName()));

        return false;
    }
}
