package ca.ckay9.Commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.ckay9.Config;
import ca.ckay9.CxWar;
import ca.ckay9.Utils;

public class RevealCommand implements CommandExecutor {
    private CxWar cx_war;
    private HashMap<UUID, Integer> reveal_cooldowns;

    public RevealCommand(CxWar cx_war) {
        this.cx_war = cx_war;
        this.reveal_cooldowns = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            reveal_cooldowns.put(player.getUniqueId(), 0);
        }

        cx_war.getServer().getScheduler().scheduleSyncRepeatingTask(cx_war, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (reveal_cooldowns.get(player.getUniqueId()) == null) {
                        reveal_cooldowns.put(player.getUniqueId(), 0);
                    }

                    int current_cooldown = reveal_cooldowns.get(player.getUniqueId());
                    if (current_cooldown <= 0) {
                        continue;
                    }

                    reveal_cooldowns.put(player.getUniqueId(), current_cooldown - 1);
                }
            }
        }, 0, 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length <= 0) {
            player.sendMessage(Utils.formatText("&cInvalid target user: /reveal [PLAYER_NAME]"));
            return false;
        }

        String target_player_name = args[0];
        Player target_player = Bukkit.getPlayerExact(target_player_name);
        if (target_player == null) {
            player.sendMessage(Utils.formatText("&cInvalid target user: /reveal [PLAYER_NAME]"));
            return false;
        }

        if (reveal_cooldowns.get(player.getUniqueId()) == null) {
            reveal_cooldowns.put(player.getUniqueId(), 0);
        }

        int current_cooldown = reveal_cooldowns.get(player.getUniqueId());
        if (current_cooldown > 0) {
            player.sendMessage(Utils.formatText("&cReveal on cooldown: " + current_cooldown + "s"));
            return false;
        }

        Location player_location = player.getLocation();
        Location target_location = target_player.getLocation();

        // Alert players of target's location    
        Bukkit.broadcastMessage(Utils.formatText("&9" + target_player.getName() + "'s location has been leaked: " + target_location.getBlockX() + ", " + target_location.getBlockY() + ", " + target_location.getBlockZ()));
        
        // Alert server to location of player
        Bukkit.broadcastMessage(Utils.formatText("&eDue to leaking another player's location, " + player.getName() + " has revealed their own location: " + player_location.getBlockX() + ", " + player_location.getBlockY() + ", " + player_location.getBlockZ()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * Config.data.getInt("reveal.cooldown", 180), 255));
 
        // Update cooldown
        reveal_cooldowns.put(player.getUniqueId(), Config.data.getInt("reveal.cooldown", 180));

        
        return false;
    }

}
