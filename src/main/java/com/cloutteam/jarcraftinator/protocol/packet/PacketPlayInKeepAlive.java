package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInKeepAlive extends PacketIn {

    private long keepAliveId;

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException {
        keepAliveId = in.readLong();
    }

}
