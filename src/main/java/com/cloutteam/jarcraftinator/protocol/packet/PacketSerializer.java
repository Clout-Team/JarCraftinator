package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.world.BlockState;
import com.cloutteam.jarcraftinator.world.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class PacketSerializer {

    // Chunk data constants
    private static final int CHUNK_HEIGHT = 256;
    private static final int SECTION_HEIGHT = 16;
    private static final int SECTION_WIDTH = 16;
    private static final byte FULL_SIZE_BITS_PER_BLOCK = 13;
    private byte[] packetId;
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public PacketSerializer withPacketId(int id) {
        packetId = getVarInt(id);
        return this;
    }

    public PacketSerializer writeBytes(byte... bytes) throws IOException {
        stream.write(bytes);
        return this;
    }

    public PacketSerializer writeBoolean(boolean bool) {
        stream.write((byte) (bool ? 1 : 0));
        return this;
    }

    public PacketSerializer writeShort(short s) throws IOException {
        stream.write(ByteBuffer.allocate(Short.BYTES).order(ByteOrder.BIG_ENDIAN).putShort(s).array());
        return this;
    }

    public PacketSerializer writeInt(int integer) throws IOException {
        stream.write(ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN).putInt(integer).array());
        return this;
    }

    public PacketSerializer writeLong(long l) throws IOException {
        stream.write(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(l).array());
        return this;
    }

    public PacketSerializer writeFloat(float f) throws IOException {
        stream.write(ByteBuffer.allocate(Float.BYTES).order(ByteOrder.BIG_ENDIAN).putFloat(f).array());
        return this;
    }

    public PacketSerializer writeDouble(double d) throws IOException {
        stream.write(ByteBuffer.allocate(Double.BYTES).order(ByteOrder.BIG_ENDIAN).putDouble(d).array());
        return this;
    }

    public PacketSerializer writeString(String s) throws IOException {
        byte[] str = s.getBytes(Charset.forName("UTF-8"));
        writeVarInt(str.length);
        stream.write(str);
        return this;
    }

    public PacketSerializer writeVarInt(int integer) throws IOException {
        stream.write(getVarInt(integer));
        return this;
    }

    public PacketSerializer writePosition(int x, int y, int z) throws IOException {
        return writeLong(((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF));
    }

    public PacketSerializer writeChunk(Chunk chunk) throws IOException {
        writeInt(chunk.getX()).writeInt(chunk.getZ()).writeBoolean(true);

        int mask = 0;
        ByteArrayOutputStream sections = new ByteArrayOutputStream();
        for (int sectionY = 0; sectionY < CHUNK_HEIGHT / SECTION_HEIGHT; sectionY++)
            if (!chunk.isSectionEmpty(sectionY)) {
                mask |= (1 << sectionY);
                writeChunkSection(chunk.getSection(sectionY), sections);
            }
        for (int z = 0; z < SECTION_WIDTH; z++)
            for (int x = 0; x < SECTION_WIDTH; x++)
                sections.write(chunk.getBiome(x, z).getId());
        writeVarInt(mask);
        writeVarInt(sections.size());
        writeBytes(sections.toByteArray());

        // Block entities aren't yet supported.
        writeVarInt(0);
        return this;
    }

    private PacketSerializer writeChunkSection(Chunk.ChunkSection chunkSection, ByteArrayOutputStream stream) throws IOException {
        // Block palette isn't implemented yet
        stream.write(FULL_SIZE_BITS_PER_BLOCK);
        stream.write(getVarInt(0));


        // Calculate block data size
        stream.write(getVarInt(FULL_SIZE_BITS_PER_BLOCK * 64));

        long currentLong = 0;
        int bitsWritten = 0;
        for (int y = 0; y < SECTION_HEIGHT; y++)
            for (int z = 0; z < SECTION_WIDTH; z++)
                for (int x = 0; x < SECTION_WIDTH; x++) {
                    BlockState block = chunkSection.getState(x, y, z);
                    if (bitsWritten + FULL_SIZE_BITS_PER_BLOCK <= 64) {
                        currentLong |= (((long) (block.getId() << 4 | block.getMetadata())) << bitsWritten);
                        bitsWritten += FULL_SIZE_BITS_PER_BLOCK;
                    } else {
                        int newLong = FULL_SIZE_BITS_PER_BLOCK - 64 + bitsWritten;
                        currentLong |= (((long) (block.getId() << 4 | block.getMetadata())) << bitsWritten);
                        stream.write(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(currentLong).array());
                        currentLong = 0;
                        currentLong |= ((long) (block.getId() << 4 | block.getMetadata())) >> FULL_SIZE_BITS_PER_BLOCK - newLong;
                        bitsWritten = newLong;
                    }
                    if (bitsWritten == 64) {
                        stream.write(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(currentLong).array());
                        currentLong = 0;
                        bitsWritten = 0;
                    }
                }

        // Block lighting
        for (int y = 0; y < SECTION_HEIGHT; y++)
            for (int z = 0; z < SECTION_WIDTH; z++)
                for (int x = 0; x < SECTION_WIDTH; x += 2) {
                    // Note: x += 2 above; we read 2 values along x each time
                    int value = chunkSection.getBlockLight(x, y, z) | (chunkSection.getBlockLight(x + 1, y, z) << 4);
                    stream.write(value);
                }

        // Sky lighting
        if (chunkSection.getWorld().hasSkylight())
            for (int y = 0; y < SECTION_HEIGHT; y++)
                for (int z = 0; z < SECTION_WIDTH; z++)
                    for (int x = 0; x < SECTION_WIDTH; x += 2) {
                        // Note: x += 2 above; we read 2 values along x each time
                        int value = chunkSection.getSkyLight(x, y, z) | (chunkSection.getSkyLight(x + 1, y, z) << 4);
                        stream.write(value);
                    }
        return this;
    }


    public void send(DataOutputStream stream) throws IOException {
        byte[] bytes = this.stream.toByteArray();
        stream.write(getVarInt(packetId.length + bytes.length));
        stream.write(packetId);
        stream.write(bytes);
        stream.flush();
    }


    private byte[] getVarInt(int paramInt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write((byte) paramInt);
                return out.toByteArray();
            }

            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

}
