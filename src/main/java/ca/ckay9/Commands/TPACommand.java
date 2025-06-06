package ca.ckay9.Commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Teleports;
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
        
        Integer personal_combat_log = this.cx_war.teleports.combat_logs.get(player.getUniqueId());
        if (personal_combat_log != null && personal_combat_log > 1) {
            player.sendMessage(
                    Utils.formatText("&cCombat logged for &c&l" + personal_combat_log + "s"));
            return false;
        } else {
            this.cx_war.teleports.combat_logs.put(player.getUniqueId(), 0);
        }


        String target_name = args[0];
        Player target_player = Bukkit.getPlayerExact(target_name);
        if (target_player == null) {
            player.sendMessage(
                    Utils.formatText("&cUnable to find target player"));
            return false;
        }

        if (!Teleports.playerHasNoPlayersInRadius(target_player, this.cx_war)) {
            player.sendMessage(
                    Utils.formatText("&cCan't accept teleport while they are near other players!"));
            return false;
        }

        Integer target_combat_log = this.cx_war.teleports.combat_logs.get(target_player.getUniqueId());
        if (target_combat_log != null && target_combat_log > 1) {
            player.sendMessage(
                Utils.formatText("&cTarget is combat logged for &c&l" + target_combat_log + "s"));

            return false;
        } else {
            this.cx_war.teleports.combat_logs.put(target_player.getUniqueId(), 0);
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
