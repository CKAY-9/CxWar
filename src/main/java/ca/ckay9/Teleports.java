package ca.ckay9;

import java.util.HashMap;
import java.util.UUID;

public class Teleports {
    private CxWar cx_war;
    public HashMap<UUID, UUID> requests;

    public Teleports(CxWar cx_war) {
        this.cx_war = cx_war;
        this.requests = new HashMap<>();
    }

    
}
