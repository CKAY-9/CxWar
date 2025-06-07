package ca.ckay9.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Storage;
import ca.ckay9.Teleports;
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

        if (!Storage.config.getBoolean("tp.enabled", true)) {
            sender.sendMessage(Utils.formatText("&c&lTPR &r&cis disabled on this server"));
            return false;
        }

        Player player = (Player) sender;
        if (args.length <= 0) {
            player.sendMessage(
                    Utils.formatText("&cInvalid tpr usage: /tpr [player]"));
            return false;
        }

        if (!Teleports.playerHasNoPlayersInRadius(player, this.cx_war)) {
            player.sendMessage(
                    Utils.formatText("&cCan't teleport while a player is near you!"));
            return false;
        }

        if (Storage.config.getBoolean("combat_logging.enabled", true)) {
            Integer personal_combat_log = this.cx_war.combat_logs.get(player.getUniqueId());
            if (personal_combat_log != null && personal_combat_log > 1) {
                player.sendMessage(
                        Utils.formatText("&cCombat logged for &c&l" + personal_combat_log + "s"));
                return false;
            } else {
                this.cx_war.combat_logs.put(player.getUniqueId(), 0);
            }
        }

        String target_name = args[0];
        Player target_player = Bukkit.getPlayerExact(target_name);
        if (target_player == null) {
            player.sendMessage(
                    Utils.formatText("&cUnable to find target player"));
            return false;
        }

        if (Storage.config.getBoolean("combat_logging.enabled", true)) {
            Integer target_combat_log = this.cx_war.combat_logs.get(target_player.getUniqueId());
            if (target_combat_log != null && target_combat_log > 1) {
                player.sendMessage(
                        Utils.formatText("&cTarget is combat logged for &c&l" + target_combat_log + "s"));

                return false;
            } else {
                this.cx_war.combat_logs.put(target_player.getUniqueId(), 0);
            }
        }

        this.cx_war.teleports.requests.put(player.getUniqueId(), target_player.getUniqueId());
        target_player.sendMessage(Utils.formatText(
                "&a&l" + player.getName() + "&r&a has sent a teleport request. Type &a&l/tpa " + player.getName()));
        player.sendMessage(Utils.formatText("&aSent a teleport request to &a&l" + target_player.getName()));

        return false;
    }
}
