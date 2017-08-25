package com.cloutteam.jarcraftinator.manager;

import com.cloutteam.jarcraftinator.world.navigation.Teleport;

import java.util.ArrayList;
import java.util.List;

public class TeleportManager {

    private List<Teleport> teleportList = new ArrayList<>();

    /**
     * Adds a teleport to the teleport list.
     *
     * @param teleport A teleport object.
     */
    public void addTeleport(Teleport teleport) {
        teleportList.add(teleport);
    }

    /**
     * Gets a teleport from it's ID.
     *
     * @param id The ID of the teleport. Must be from 0 to 2147483647.
     * @return The teleport object associated with that ID. If there is no such teleport object, then null is returned.
     */
    public Teleport getTeleport(int id) {
        for (Teleport teleport : teleportList)
            if (teleport.getId() == id) return teleport;
        return null;
    }

    /**
     * Indicates if the teleport is confirmed or not from it's ID.
     *
     * @param id The ID of the teleport. Must be from 0 to 2147483647.
     * @return False if there is no teleport with such ID, or if the teleport wasn't confirmed by the player. True otherwise.
     */
    public boolean isTeleportConfirmed(int id) {
        Teleport teleport = getTeleport(id);
        return teleport != null && teleport.isConfirmed();
    }

    /**
     * Indicates if a teleport ID exists or not.
     *
     * @param id The ID of the teleport. Must be from 0 to 2147483647.
     * @return False if the teleport doesn't exist. True otherwise.
     */
    public boolean exists(int id) {
        return getTeleport(id) != null;
    }

    /**
     * Confirms a teleport.
     *
     * @param id The ID of the teleport. Must be from 0 to 2147483647.
     * @return False if the teleport doesn't exist or if it's already confirmed. True otherwise.
     */
    public boolean confirmTeleport(int id) {
        Teleport teleport = getTeleport(id);
        if (teleport == null || teleport.isConfirmed()) return false;
        teleport.confirm();
        return true;
    }

}
