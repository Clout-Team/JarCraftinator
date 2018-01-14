package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class PacketIn extends Packet {

    public abstract void onReceive(int length, DataInputStream in) throws IOException;

}
