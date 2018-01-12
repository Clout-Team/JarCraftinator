package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketStatusInPing extends PacketIn {

    private int length;
    private long data;

    public int getLength() {
        return length;
    }

    public long getData() {
        return data;
    }

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException {
        this.length = length;
        data = in.readLong();
    }
}
