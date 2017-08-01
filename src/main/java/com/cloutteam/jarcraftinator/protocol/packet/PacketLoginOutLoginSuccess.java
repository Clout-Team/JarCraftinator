package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataOutputStream;
import java.util.UUID;

public class PacketLoginOutLoginSuccess extends PacketOut{

    private UUID uuid;
    private String username;

    public PacketLoginOutLoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void send(DataOutputStream out) {
        try {
            byte[] packetId = VarData.getVarInt(0x02);
            byte[] uuid = VarData.packString(this.uuid.toString());
            byte[] username = VarData.packString(this.username);

            VarData.writeVarInt(out, packetId.length + uuid.length + username.length);
            out.write(packetId);
            out.write(uuid);
            out.write(username);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
