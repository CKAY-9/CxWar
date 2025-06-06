package ca.ckay9.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import ca.ckay9.CxWar;
import ca.ckay9.Storage;

public class PlayerDamage implements Listener {
    private CxWar cx_war;

    public PlayerDamage(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getEntity();
        cx_war.teleports.combat_logs.put(player.getUniqueId(), Storage.config.getInt("tp.combat_log_time", 300));
    }
}
