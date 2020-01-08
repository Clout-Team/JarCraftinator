package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataOutputStream;
import java.io.IOException;

public class PacketStatusOutPong extends PacketOut {

    private int length;
    private long data;

    public PacketStatusOutPong(int length, long data) {
        this.length = length;
        this.data = data;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
        VarData.writeVarInt(out, length);
        VarData.writeVarInt(out, MinecraftPacket.STATUS.PONG);
        out.writeLong(data);
        out.flush();
        System.out.println("PacketStatusOutPong");
    }
}
