package com.cloutteam.jarcraftinator.packet;

import java.io.DataOutputStream;

public abstract class PacketOut extends Packet {

    public abstract void send(DataOutputStream out);

}
