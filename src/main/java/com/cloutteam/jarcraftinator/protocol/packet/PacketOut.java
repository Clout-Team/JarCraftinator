package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.DataOutputStream;

public abstract class PacketOut extends Packet {

    public abstract void send(DataOutputStream out);

}
