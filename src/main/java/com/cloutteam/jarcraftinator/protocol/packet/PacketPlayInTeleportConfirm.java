package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInTeleportConfirm extends PacketIn {

    private int teleportID;

    public int getTeleportID() {
        return teleportID;
    }

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException {
        teleportID = VarData.readVarInt(in);
    }
}
