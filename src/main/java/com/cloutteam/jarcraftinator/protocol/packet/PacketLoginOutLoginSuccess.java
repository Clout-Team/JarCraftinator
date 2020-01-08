package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PacketLoginOutLoginSuccess extends PacketOut{

    private UUID uuid;
    private String username;

    public PacketLoginOutLoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
        byte[] packetId = VarData.getVarInt(MinecraftPacket.LOGIN.LOGIN_SUCCESS.out);
        byte[] uuid = VarData.packString(this.uuid.toString());
        byte[] username = VarData.packString(this.username);

        System.out.println("UUID: " + this.uuid.toString());
        System.out.println("Username: " + this.username);

        VarData.writeVarInt(out, packetId.length + uuid.length + username.length);
        out.write(packetId);
        out.write(uuid);
        out.write(username);
        out.flush();
        System.out.println("PacketLoginOutLoginSuccess");
    }
}
