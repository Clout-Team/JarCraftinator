package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.world.Chunk;

import java.io.IOException;

public class PacketPlayOutChunkData extends PacketOut {

    private Chunk chunk;

    public PacketPlayOutChunkData(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void send(PacketSerializer serializer) throws IOException {
        serializer.withPacketId(0x20).writeChunk(chunk);
    }
}
