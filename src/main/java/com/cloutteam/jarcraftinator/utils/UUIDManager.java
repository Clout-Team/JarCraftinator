package com.cloutteam.jarcraftinator.utils;

import com.cloutteam.jarcraftinator.JARCraftinator;

import java.util.UUID;

public class UUIDManager {

    public static UUID getUUID(String username) {
        if(JARCraftinator.getConfig().getBoolean("online-mode")){
            return null;
        }else {
            //For now, we'll use offline UUIDs. Online mode will be implemented later.
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
        }
    }

}
