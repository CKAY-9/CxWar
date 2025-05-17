package ca.ckay9;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Killstreaks {
    private CxWar cx_war;
    private HashMap<UUID, Integer> player_killstreaks;

    public Killstreaks(CxWar cx_war) {
        this.cx_war = cx_war;
        this.player_killstreaks = new HashMap<>();

        if (Storage.data.getConfigurationSection("killstreaks") != null) {
            for (String uuid : Storage.data.getConfigurationSection("killstreaks").getKeys(false)) {
                UUID converted = UUID.fromString(uuid);
                player_killstreaks.put(converted, Storage.data.getInt("killstreaks." + uuid, 0));
            }
        }
    }

    public HashMap<UUID, Integer> getPlayerKillstreaks() {
        return this.player_killstreaks;
    }

    public void updatePlayerKillstreaks(UUID player_uuid, int value) {
        this.player_killstreaks.put(player_uuid, value);

        try {
            Storage.data.set("killstreaks." + player_uuid.toString(), value);
            Storage.data.save(Storage.data_file);
        } catch (IOException exception) {
            exception.printStackTrace();
        } 
    }
}
