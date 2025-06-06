package ca.ckay9;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
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
}
