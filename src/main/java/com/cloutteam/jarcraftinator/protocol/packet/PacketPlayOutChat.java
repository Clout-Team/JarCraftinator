package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.IOException;

public class PacketPlayOutChat extends PacketOut {

    private String chatComponent;

    public PacketPlayOutChat(String chatComponent) {
        this.chatComponent = chatComponent;
    }

    @Override
    public void send(PacketSerializer serializer) throws IOException {
        serializer.withPacketId(0x0F).writeString(chatComponent).writeBytes((byte) 0x00);
    }

}
