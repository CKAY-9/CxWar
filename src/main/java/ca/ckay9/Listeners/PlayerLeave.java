package ca.ckay9.Listeners;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ca.ckay9.CxWar;
import ca.ckay9.Storage;

public class PlayerLeave implements Listener {
    private CxWar cx_war;

    public PlayerLeave(CxWar cx_war) {
        this.cx_war = cx_war;
    }

    @EventHandler
    public void saveLocation(PlayerQuitEvent event) {
        Player player = event.getPlayer();     
        try {
            Location loc = player.getLocation();
            Storage.data.set("logoff_locations." + player.getUniqueId() + ".name", player.getName());
            Storage.data.set("logoff_locations." + player.getUniqueId() + ".position", new int[]{
                loc.getBlockX(), 
                loc.getBlockY(), 
                loc.getBlockZ()
            });
            Storage.data.save(Storage.data_file);
        } catch (IOException exception) {
            exception.printStackTrace();
        } 
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void removeTeleport(PlayerQuitEvent event) {
        if (!Storage.config.getBoolean("tp.enabled", true)) {
            return;
        }
        
        Player player = event.getPlayer();

        if (this.cx_war.teleports.requests.get(player.getUniqueId()) != null) {
            this.cx_war.teleports.requests.remove(player.getUniqueId());
        }
    } 
}
