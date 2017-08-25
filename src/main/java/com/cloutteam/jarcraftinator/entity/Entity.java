package com.cloutteam.jarcraftinator.entity;

import com.cloutteam.jarcraftinator.world.World;
import com.cloutteam.jarcraftinator.world.navigation.Location;
import com.cloutteam.jarcraftinator.world.navigation.Teleport;

public class Entity {

    private static int lastEntityId = 0;

    private final int entityId;
    protected Location currentLocation;

    public Entity() {
        entityId = lastEntityId++;
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getLocation() {
        return currentLocation;
    }

    public World getWorld() {
        return currentLocation.getWorld();
    }

    public Teleport teleport(Location location) {
        return teleport(location, Teleport.TeleportCause.PLUGIN);
    }

    protected void updateLocation(Location newLocation, Teleport teleport) {
        updateLocation(newLocation);
    }

    protected void updateLocation(Location newLocation) {
        currentLocation = newLocation;
        //TODO send Entity Teleport packet to all online players
    }

    public Teleport teleport(Location location, Teleport.TeleportCause cause) {
        Teleport teleport = new Teleport(this, currentLocation, location, cause);
        updateLocation(location, teleport);
        //TODO fire teleport event
        return teleport;
    }

}
