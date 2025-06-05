package ca.ckay9.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Utils;

public class TPACommand implements CommandExecutor{
    private CxWar cx_war;

    public TPACommand(CxWar cx_war) {
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

        UUID requesting = this.cx_war.teleports.requests.get(target_player.getUniqueId());
        if (requesting == null) {
            player.sendMessage(
                    Utils.formatText("&cUnable to find target request"));
            return false;
        }

        target_player.teleport(player.getLocation());
        player.sendMessage(Utils.formatText("&a&l" + target_player.getName() + "&r&a has teleported to you"));
        target_player.sendMessage(Utils.formatText("&aYou have teleported to &a&l" + player.getName()));

        return false;
    }
}
