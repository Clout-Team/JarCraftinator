package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutChunkData extends PacketOut {

    private Chunk chunk;

    public PacketPlayOutChunkData(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream chunkStream = new DataOutputStream(byteArrayOutputStream);
            VarData.writeVarInt(chunkStream, MinecraftPacket.PLAY.CHUNK_DATA.out);
            VarData.writeChunkDataPacket(chunk, chunkStream);

            // Send actual packet
            VarData.writeVarInt(out, byteArrayOutputStream.size()-7);
            out.write(byteArrayOutputStream.toByteArray());
            out.flush();
    }
}
