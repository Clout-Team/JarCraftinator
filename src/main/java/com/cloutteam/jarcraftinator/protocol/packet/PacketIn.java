package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.DataInputStream;

public abstract class PacketIn extends Packet {

    public abstract void onReceive(int length, DataInputStream in) throws Exception;

}
