package com.cloutteam.jarcraftinator.utils;

import java.util.UUID;

public class UUIDManager {

    public static UUID getUUID(String username) {
        //For now, we'll use offline UUIDs. Online mode will be implemented later.
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
    }

}
