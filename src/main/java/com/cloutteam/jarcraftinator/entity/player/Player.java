package com.cloutteam.jarcraftinator.entity.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.protocol.packet.PacketPlayOutPlayerPositionAndLook;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.World;
import com.cloutteam.jarcraftinator.world.navigation.Location;
import com.cloutteam.jarcraftinator.world.navigation.Teleport;

import java.io.IOException;
import java.util.UUID;

public class Player extends OfflinePlayer {

    private final PlayerConnection playerConnection;

    public Player(String name, UUID uuid, PlayerConnection connection) {
        super(name, uuid);
        this.playerConnection = connection;
    }

    public PlayerConnection getPlayerConnection() {
        return playerConnection;
    }

    @Override
    protected void updateLocation(Location newLocation, Teleport teleport) {
        try {
            if (newLocation.equals(currentLocation)) return;
            new PacketPlayOutPlayerPositionAndLook(currentLocation, (byte) 0, teleport.getId()).send(this);
            currentLocation = newLocation;

            //TODO send entity teleport packet to the other players
        }catch(IOException ex){
            JARCraftinator.getLogger().log("Error sending PacketPlayOutPlayerPositionAndLook to "
                    + getName() + " (" + ex.getMessage() + ")", LogLevel.DEBUG);
        }
    }

    @Override
    protected void updateLocation(Location newLocation) {
        if (!newLocation.equals(currentLocation))
            currentLocation = newLocation;
    }

    public void loadFromStorage() {
        //TODO load player data from the storage files
        currentLocation = new Location(new World("world", DimensionType.OVERWORLD), 0, 128, 0);
    }

}
