package ca.ckay9.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.Utils;

public class WhisperCommand implements CommandExecutor {
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Utils.formatText("&c Invalid whisper usage: /whisper [name] [message]"));
            return false;
        }

        String target_player_name = args[0];
        Player player = Bukkit.getPlayerExact(target_player_name);
        if (player == null) {
            sender.sendMessage(Utils.formatText("&cCouldn't find a player by that name."));
            return false;
        }

        String message = Utils.combineStringArrayIntoSingle(args, 1);

        player.sendMessage(Utils.formatText("&7" + sender.getName() + " whispered to you: " + message));
        sender.sendMessage(Utils.formatText("&7Whispered to " + player.getName() + ": " + message));
        
        return false;
    }
}
