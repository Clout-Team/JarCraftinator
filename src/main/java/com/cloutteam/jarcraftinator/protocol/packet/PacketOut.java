package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.entity.player.Player;

import java.io.DataOutputStream;

public abstract class PacketOut extends Packet {

    public abstract void send(DataOutputStream out);

    public void send(Player p) {
        send(p.getPlayerConnection().getOut());
    }

}
