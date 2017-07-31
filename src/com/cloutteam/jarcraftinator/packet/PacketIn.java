package com.cloutteam.jarcraftinator.packet;

import java.io.DataInputStream;

public abstract class PacketIn extends Packet {

    public abstract void onReceive(DataInputStream in) throws Exception;

}
