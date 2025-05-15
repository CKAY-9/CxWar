package ca.ckay9;

import java.util.HashMap;
import java.util.UUID;

public class Killstreaks {
    private CxWar cx_war;
    private HashMap<UUID, Integer> player_killstreaks;

    public Killstreaks(CxWar cx_war) {
        this.cx_war = cx_war;
        this.player_killstreaks = new HashMap<>();
    }

    public HashMap<UUID, Integer> getPlayerKillstreaks() {
        return this.player_killstreaks;
    }

    public void updatePlayerKillstreaks(UUID player_uuid, int value) {
        this.player_killstreaks.put(player_uuid, value);
    }
}
