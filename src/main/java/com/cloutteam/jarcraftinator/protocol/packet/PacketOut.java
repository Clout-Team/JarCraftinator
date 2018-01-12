package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.entity.player.Player;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class PacketOut extends Packet {

    public abstract void send(DataOutputStream out) throws IOException;

    public void send(Player p) throws IOException {
        send(p.getPlayerConnection().getOut());
    }

}
