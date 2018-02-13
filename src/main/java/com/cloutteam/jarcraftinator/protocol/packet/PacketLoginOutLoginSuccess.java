package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.logging.LogLevel;

import java.io.IOException;
import java.util.UUID;

public class PacketLoginOutLoginSuccess extends PacketOut {

    private UUID uuid;
    private String username;

    public PacketLoginOutLoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void send(PacketSerializer serializer) throws IOException {
        serializer.withPacketId(0x02).writeString(this.uuid.toString()).writeString(this.username);
        JARCraftinator.getLogger().log("UUID: " + this.uuid.toString(), LogLevel.DEBUG);
        JARCraftinator.getLogger().log("Username: " + this.username, LogLevel.DEBUG);
    }
}
