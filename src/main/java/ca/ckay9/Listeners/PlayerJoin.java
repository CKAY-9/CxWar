package ca.ckay9.Listeners;

import java.io.IOException;
import java.util.UUID;

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void leaveWhileCombatLogged(PlayerJoinEvent event) {
        if (!Storage.config.getBoolean("combat_logging.enabled", true)
                || !Storage.config.getBoolean("combat_logging.die_if_leave", true)) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Storage.data.getBoolean("dol." + uuid)) {
            this.cx_war.die_on_leave.add(uuid);
        }

        boolean should_die_on_leave = this.cx_war.die_on_leave.contains(player.getUniqueId());
        if (!should_die_on_leave) {
            return;
        } else {
            this.cx_war.die_on_leave.remove(player.getUniqueId());
        }

        player.setHealth(0);

        try {
            Storage.data.set("dol." + player.getUniqueId().toString(), null);
            Storage.data.save(Storage.data_file);
        } catch (IOException exception) {
            this.cx_war.getLogger().warning(exception.toString());
        }
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
