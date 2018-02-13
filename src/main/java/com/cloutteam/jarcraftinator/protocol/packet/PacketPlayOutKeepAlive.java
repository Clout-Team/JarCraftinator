package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.IOException;

public class PacketPlayOutKeepAlive extends PacketOut {

    @Override
    public void send(PacketSerializer serializer) throws IOException {
        serializer.withPacketId(0x1F).writeLong(System.currentTimeMillis());
    }

}
