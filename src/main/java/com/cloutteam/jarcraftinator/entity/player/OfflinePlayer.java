package com.cloutteam.jarcraftinator.entity.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.entity.Entity;

import java.util.UUID;

public class OfflinePlayer extends Entity {

    private final String name;
    private final UUID onlineUUID;
    private final UUID offlineUUID;

    private boolean hasJoinedBefore = true;

    public OfflinePlayer(String name, UUID uuid) {
        this.name = name;
        this.onlineUUID = uuid;
        this.offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return JARCraftinator.getINSTANCE().getConfigManager().isOnlineMode() ? onlineUUID : offlineUUID;
    }

    public UUID getOnlineUUID() {
        return onlineUUID;
    }

    public UUID getOfflineUUID() {
        return offlineUUID;
    }

    public boolean hasJoinedBefore() {
        return hasJoinedBefore;
    }

    public void setHasJoinedBefore(boolean hasJoinedBefore) {
        this.hasJoinedBefore = hasJoinedBefore;
    }
}
