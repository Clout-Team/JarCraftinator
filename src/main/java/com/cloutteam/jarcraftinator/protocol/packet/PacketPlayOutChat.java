package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutChat extends PacketOut {

    private final PacketPlayOutChatPosition position;
    private final String chatComponent;

    public PacketPlayOutChat(String chatComponent, PacketPlayOutChatPosition position){
        this.position = position;
        this.chatComponent = chatComponent;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {

        byte[] packetId = VarData.getVarInt(MinecraftPacket.PLAY.CHAT.out);

        // Prepare packet data
        ByteArrayOutputStream packetData = new ByteArrayOutputStream();
        DataOutputStream packetDataWriter = new DataOutputStream(packetData);

        VarData.writeVarString(packetDataWriter, chatComponent);
        packetDataWriter.writeByte(position.getId());

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

    public enum PacketPlayOutChatPosition {

        CHAT_BOX(0x00),
        SYSTEM_MESSAGE(0x01),
        HOTBAR(0x02);

        private final byte id;

        PacketPlayOutChatPosition(int id){
            this.id = (byte) id;
        }

        public byte getId() {
            return id;
        }
    }

}