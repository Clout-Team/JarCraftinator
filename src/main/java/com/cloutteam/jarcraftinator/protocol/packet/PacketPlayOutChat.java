package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutChat extends PacketOut {

    private String chatComponent;

    public PacketPlayOutChat(String chatComponent){
        this.chatComponent = chatComponent;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        byte[] packetId = VarData.getVarInt(0x0F);

        // Begin packet data
        VarData.writeVarString(dataOutputStream, chatComponent);
        dataOutputStream.writeByte(0x00); // 0 = chat box
        // End packet data

        // Flush packet data
        dataOutputStream.flush();
        dataOutputStream.close();
        byteArrayOutputStream.flush();

        // Send the entire packet to the client
        VarData.writeVarInt(out, packetId.length + dataOutputStream.size());
        out.write(packetId);
        out.write(byteArrayOutputStream.toByteArray());
        out.flush();

        // Close our temporary streams
        byteArrayOutputStream.close();
    }

}
