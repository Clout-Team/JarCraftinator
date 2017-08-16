package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class PacketPlayOutChunkData extends PacketOut {

    private Chunk chunk;

    public PacketPlayOutChunkData(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void send(DataOutputStream out) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream chunkStream = new DataOutputStream(byteArrayOutputStream);
            VarData.writeVarInt(chunkStream, 0x20);
            VarData.writeChunkDataPacket(chunk, chunkStream);

            // Send actual packet
            VarData.writeVarInt(out, byteArrayOutputStream.size());
            out.write(byteArrayOutputStream.toByteArray());
            System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
