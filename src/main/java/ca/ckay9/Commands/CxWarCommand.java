package ca.ckay9.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ca.ckay9.Storage;
import ca.ckay9.Utils;

public class CxWarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Utils.formatText("&9&lCxWar&r&9 by CKAY9 - &9&l" + Utils.getPlugin().getDescription().getVersion()));
        if (Storage.config.getBoolean("reveal.enabled", true)) {
            sender.sendMessage(Utils.formatText("&e - /reveal: Reveal a player's location"));
        }
        if (Storage.config.getBoolean("hidden.enabled", true)) {
            sender.sendMessage(Utils.formatText("&e - /hidden: Hide from potential reveals"));
        }
        if (Storage.config.getBoolean("groups.enabled", true)) {
            sender.sendMessage(Utils.formatText("&e - /group: Create a group with others"));
        }
        if (Storage.config.getBoolean("killstreaks.enabled", true)) {
            sender.sendMessage(Utils.formatText("&e - /killstreak: See your own or other's current killstreak"));
        }
        if (Storage.config.getBoolean("whisper.enabled", true)) {
            sender.sendMessage(Utils.formatText("&e - /whisper: Secretly chat with other players"));
        }
        if (Storage.config.getBoolean("tp.enabled", true)) {
            sender.sendMessage(Utils.formatText("&e - /tpr: Send a teleport request to a player"));
            sender.sendMessage(Utils.formatText("&e - /tpa: Accept a teleport request from a player"));
        }

        return false;
    }

}
