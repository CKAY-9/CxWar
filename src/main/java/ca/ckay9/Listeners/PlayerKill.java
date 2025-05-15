package ca.ckay9.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import ca.ckay9.CxWar;
import ca.ckay9.Utils;

public class PlayerKill implements Listener {
    private CxWar cx_war;

    public PlayerKill(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player dead_player = event.getEntity();
        if (!(dead_player.getKiller() instanceof Player)) {
            return;
        }

        Player killer_player = dead_player.getKiller();

        HashMap<UUID, Integer> killstreaks = this.cx_war.killstreaks.getPlayerKillstreaks();
        int killer_streak = killstreaks.get(killer_player.getUniqueId());
        int dead_streak = killstreaks.get(dead_player.getUniqueId());

        if (dead_streak >= 5) {
            Bukkit.broadcastMessage(Utils.formatText(killer_player.getDisplayName() + " &c has ended &r"
                    + dead_player.getDisplayName() + "' killstreak of &c&l" + dead_streak));
        }

        if ((killer_streak + 1) >= 5) {
            Bukkit.broadcastMessage(Utils
                    .formatText(killer_player.getDisplayName() + " &c now has a killstreak of &c&l" + (killer_streak + 1)));
        }

        this.cx_war.killstreaks.updatePlayerKillstreaks(killer_player.getUniqueId(), killer_streak + 1);
        this.cx_war.killstreaks.updatePlayerKillstreaks(dead_player.getUniqueId(), 0);
    }
}
