package ca.ckay9.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.ckay9.Storage;
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

        HiddenPlayer target_hidden = this.cx_war.hidden.getHiddenPlayers().get(target_player.getUniqueId());
        if (target_hidden == null) {
            target_hidden = new HiddenPlayer(0, Storage.config.getInt("hidden.timer", 360));
            this.cx_war.hidden.addToHiddenPlayers(player.getUniqueId(), target_hidden);
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

        Random rand = new Random();
        double required_chance = Storage.config.getDouble("reveal.random_chance", 25);
        double random_roll = rand.nextDouble();
        if ((required_chance / 100) < random_roll) {
            player.sendMessage(
                    Utils.formatText("&cReveal roll failed: Required: &c&l" + (int) (100 - required_chance)
                            + "&r&c, Rolled: &c&l"
                            + (100 - (int) Math.floor(random_roll * 100))));
            reveal_cooldowns.put(player.getUniqueId(), Storage.config.getInt("reveal.cooldown", 180));

            return false;
        }

        int offset = Storage.config.getInt("reveal.reveal_range", 200);
        int half_offset = (int) Math.round(offset * 0.5);
        int x_offset = rand.nextInt(-offset, offset);
        int y_offset = rand.nextInt(-half_offset, half_offset);
        int z_offset = rand.nextInt(-offset, offset);

        switch (target_hidden.type) {
            case 0:
                Bukkit.broadcastMessage(
                        Utils.formatText("&9" + target_player.getName() + "'s location has been leaked by "
                                + player.getName() + ": " + (target_location.getBlockX() + x_offset) + ", "
                                + (target_location.getBlockY() + y_offset) + ", "
                                + (target_location.getBlockZ() + z_offset)));
                break;
            case 1:
                Bukkit.broadcastMessage(
                        Utils.formatText("&9" + target_player.getName() + "'s location has been leaked by "
                                + player.getName() + ": " + (player_location.getBlockX() + x_offset) + ", "
                                + (player_location.getBlockY() + y_offset) + ", "
                                + (player_location.getBlockZ() + z_offset)));
                break;
            case 2:
                Bukkit.broadcastMessage(
                        Utils.formatText("&9" + player.getName()
                                + " revealed their own position: " + player_location.getBlockX() + ", "
                                + player_location.getBlockY() + ", " + player_location.getBlockZ()));
            default:
                break;
        }

        // Update cooldown
        reveal_cooldowns.put(player.getUniqueId(), Storage.config.getInt("reveal.cooldown", 180));

        return false;
    }

}
