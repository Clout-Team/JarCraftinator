package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PacketPlayOutChat extends PacketOut {

    private final String chatComponent;

    public PacketPlayOutChat(String chatComponent){
        this.chatComponent = chatComponent;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {

        byte[] packetId = VarData.getVarInt(0x0F);

        // Prepare packet data
        ByteArrayOutputStream packetData = new ByteArrayOutputStream();
        DataOutputStream packetDataWriter = new DataOutputStream(packetData);

        VarData.writeVarString(packetDataWriter, chatComponent);
        packetDataWriter.writeByte(0x00);

        packetDataWriter.close();
        packetData.close();
        byte[] packetBytes = packetData.toByteArray();

        // Get packet length
        int packetLength = packetId.length + packetBytes.length;

        // Write the packet length, ID and data.
        VarData.writeVarInt(out, packetLength);
        out.write(packetId);
        out.write(packetData.toByteArray());
        out.flush();

    }

}
