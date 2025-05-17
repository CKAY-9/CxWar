package ca.ckay9.Listeners;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ca.ckay9.Storage;

public class PlayerLeave implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
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
}
