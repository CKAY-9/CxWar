package ca.ckay9.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.CxWar;
import ca.ckay9.Storage;
import ca.ckay9.Utils;

public class KillstreakCommand implements CommandExecutor {
    private CxWar cx_war;

    public KillstreakCommand(CxWar cx_war) {
        this.cx_war = cx_war;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Storage.config.getBoolean("killstreaks.enabled", true)) {
            sender.sendMessage(Utils.formatText("&c&lKillstreaks &r&care disabled on this server"));
            return false;
        }

        switch (args.length) {
            case 1:
                String target_name = args[0];
                Player player = Bukkit.getPlayerExact(target_name);
                if (player == null) {
                    sender.sendMessage(Utils.formatText("&cCouldn't find a player by that name."));
                    return false;
                }

                try {
                    int killstreak = this.cx_war.killstreaks.getPlayerKillstreaks().get(player.getUniqueId());
                    sender.sendMessage(Utils.formatText(player.getDisplayName() + "&9 has a killstreak of &9&l" + killstreak));
                } catch (Exception _exception) {
                    this.cx_war.killstreaks.updatePlayerKillstreaks(player.getUniqueId(), 0);
                    sender.sendMessage(Utils.formatText(player.getDisplayName() + "&9 has a killstreak of &9&l0"));
                }
                 
                break;
            default:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.formatText("&cYou must be a player to execute this command."));
                    return false;
                }

                Player local_player = (Player)sender;
                try {
                    int killstreak = this.cx_war.killstreaks.getPlayerKillstreaks().get(local_player.getUniqueId());
                    sender.sendMessage(Utils.formatText("&9You have a killstreak of &9&l" + killstreak));
                } catch (Exception exception) {
                    this.cx_war.killstreaks.updatePlayerKillstreaks(local_player.getUniqueId(), 0);
                    sender.sendMessage(Utils.formatText("&9You have a killstreak of &9&l0"));
                }
                break;
        }

        return false;
    }
    
}
