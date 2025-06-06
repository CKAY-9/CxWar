package ca.ckay9.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import ca.ckay9.CxWar;
import ca.ckay9.Storage;
import ca.ckay9.Utils;

public class PlayerDamage implements Listener {
    private CxWar cx_war;

    public PlayerDamage(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void combatLogHandler(EntityDamageByEntityEvent event) {
        if (!Storage.config.getBoolean("combat_logging.enabled", true)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        int log_time = Storage.config.getInt("combat_logging.duration", 30);
        HashMap<UUID, Integer> logs = this.cx_war.combat_logs;

        Player player = (Player)event.getEntity();
        UUID player_uuid = player.getUniqueId();
        if (logs.get(player_uuid) == null || logs.get(player_uuid) == 0) {
            player.sendMessage(Utils.formatText("&cYou have been combat logged for &c&l" + log_time + "s"));
        }

        cx_war.combat_logs.put(player_uuid, log_time);
        Player damager = (Player)event.getDamager();
        UUID damager_uuid = damager.getUniqueId();
        if (logs.get(damager_uuid) == null || logs.get(damager_uuid) == 0) {
            player.sendMessage(Utils.formatText("&cYou have been combat logged for &c&l" + log_time + "s"));
        }

        cx_war.combat_logs.put(damager_uuid, log_time);
    }
}
