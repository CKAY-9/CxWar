package ca.ckay9.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ca.ckay9.Utils;

public class CxWarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Utils.formatText("&9&lCxWar by CKAY9 - " + Utils.getPlugin().getDescription().getVersion()));
        sender.sendMessage(Utils.formatText("&e - /reveal: Reveal a player's location"));
        sender.sendMessage(Utils.formatText("&e - /hidden: Hide from potential reveals"));
        sender.sendMessage(Utils.formatText("&e - /group: Create a group with others"));
        sender.sendMessage(Utils.formatText("&e - /killstreak: See your own or other's current killstreak"));
        sender.sendMessage(Utils.formatText("&e - /whisper: Secretly chat with other players"));
        sender.sendMessage(Utils.formatText("&e - /tpr: Send a teleport request to a player"));
        sender.sendMessage(Utils.formatText("&e - /tpa: Accept a teleport request from a player"));

        return false;
    }
    
}
