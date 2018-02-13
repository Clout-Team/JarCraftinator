package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.IOException;

public class PacketStatusOutPong extends PacketOut {

    private long data;

    public PacketStatusOutPong(long data) {
        this.data = data;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public void send(PacketSerializer serializer) throws IOException {
        serializer.withPacketId(0x01).writeLong(data);
    }

}
