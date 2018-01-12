package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataOutputStream;

public class PacketPlayOutKeepAlive extends PacketOut {

    @Override
    public void send(DataOutputStream out){
        try {
            byte[] packetId = VarData.getVarInt(0x1F);

            VarData.writeVarInt(out, 8 + packetId.length);
            out.write(packetId);
            out.writeLong(System.currentTimeMillis());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
