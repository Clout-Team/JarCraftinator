package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketLoginInLoginStart extends PacketIn {

    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException {
        playerName = VarData.readVarString(in, VarData.readVarInt(in));
    }
}
