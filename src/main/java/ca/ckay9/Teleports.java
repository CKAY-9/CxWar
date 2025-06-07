package ca.ckay9;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Teleports {
    private CxWar cx_war;
    public HashMap<UUID, UUID> requests;
    public HashMap<UUID, Integer> combat_logs;

    public Teleports(CxWar cx_war) {
        this.cx_war = cx_war;
        this.requests = new HashMap<>();
        this.combat_logs = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            combat_logs.put(player.getUniqueId(), 5);
        }

        this.cx_war.getServer().getScheduler().scheduleSyncRepeatingTask(this.cx_war, new Runnable() {
            @Override
            public void run() {
                for (Entry<UUID, Integer> entry : combat_logs.entrySet()) {
                    if (entry.getValue() > 0) {
                        combat_logs.put(entry.getKey(), entry.getValue() - 1);
                    }
                }
            }
        }, 0, 20L);
    }

    public static boolean playerHasNoPlayersInRadius(Player player, CxWar cx_war) {
        int max_distance = Storage.config.getInt("tp.player_radius", 10);
        Location location = player.getLocation();
        Group group = Group.getPlayerGroup(player.getUniqueId(), cx_war.groups);
        for (Player other_player : Bukkit.getOnlinePlayers()) {
            if (other_player.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            if (group != null && group.isPlayerInGroup(other_player.getUniqueId())) {
                continue;
            }

            Location other_location = other_player.getLocation();
            // a^2 + b^2 + c^2 = d^2
            double distance = Math.pow(location.getBlockX() - other_location.getBlockX(), 2) +
                    Math.pow(location.getBlockY() - other_location.getBlockY(), 2) +
                    Math.pow(location.getBlockZ() - other_location.getBlockZ(), 2);
            if (distance <= max_distance * max_distance) {
                return false;
            }
        }

        return true;
    }
}
