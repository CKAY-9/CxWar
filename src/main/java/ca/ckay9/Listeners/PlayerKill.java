package ca.ckay9.Listeners;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

import ca.ckay9.CxWar;
import ca.ckay9.Storage;
import ca.ckay9.Utils;

public class PlayerKill implements Listener {
    private CxWar cx_war;

    public PlayerKill(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void deathPunishmenet(PlayerDeathEvent event) {
        if (!Storage.config.getBoolean("death.timeout_enabled", true)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Player dead_player = event.getEntity();
                int duration = Storage.config.getInt("death.timeout_duration", 300);
                BanList<PlayerProfile> ban_list = Bukkit.getBanList(BanList.Type.PROFILE);
                ban_list.addBan(dead_player.getPlayerProfile(),
                        Utils.formatText("&cDEATH TIMEOUT FOR &c&l" + duration + "s"),
                        Duration.ofSeconds(duration),
                        null);
                dead_player.kickPlayer(Utils.formatText("&cDEATH TIMEOUT FOR &c&l" + duration + "s"));
            }
        }.runTaskLater(this.cx_war, 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void killstreakHandler(PlayerDeathEvent event) {
        if (!Storage.config.getBoolean("killstreaks.enabled", true)) {
            return;
        }

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
                    .formatText(
                            killer_player.getDisplayName() + " &c now has a killstreak of &c&l" + (killer_streak + 1)));
        }

        this.cx_war.killstreaks.updatePlayerKillstreaks(killer_player.getUniqueId(), killer_streak + 1);
        this.cx_war.killstreaks.updatePlayerKillstreaks(dead_player.getUniqueId(), 0);
    }
}
