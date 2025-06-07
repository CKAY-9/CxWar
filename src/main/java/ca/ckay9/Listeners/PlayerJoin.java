package ca.ckay9.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ca.ckay9.CxWar;
import ca.ckay9.Group;
import ca.ckay9.Storage;

public class PlayerJoin implements Listener {
    private CxWar cx_war;

    public PlayerJoin(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @EventHandler
    public void killstreakJoinHanlder(PlayerJoinEvent event) {
        if (!Storage.config.getBoolean("killstreaks.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        if (this.cx_war.killstreaks.getPlayerKillstreaks().get(player.getUniqueId()) == null) {
            this.cx_war.killstreaks.updatePlayerKillstreaks(player.getUniqueId(), 0);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoinGroupHandler(PlayerJoinEvent event) {
        if (!Storage.config.getBoolean("groups.enabled", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        Group group = Group.getPlayerGroup(player.getUniqueId(), this.cx_war.groups);

        if (group != null) {
            group.setupPlayerForGroup(player);
        } else {
            Group.resetPlayerNames(player);
        }
    }
}
