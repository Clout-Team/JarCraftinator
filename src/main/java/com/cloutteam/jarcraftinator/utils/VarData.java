package com.cloutteam.jarcraftinator.utils;

import com.cloutteam.jarcraftinator.exceptions.IOWriteException;
import com.cloutteam.jarcraftinator.world.BlockState;
import com.cloutteam.jarcraftinator.world.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VarData {

    private static final int CHUNK_HEIGHT = 256;
    private static final int SECTION_HEIGHT = 16;
    private static final int SECTION_WIDTH = 16;
    private static final byte FULL_SIZE_BITS_PER_BLOCK = 13;

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        do {
            byte temp = (byte) (paramInt & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            paramInt >>>= 7;
            if (paramInt != 0) {
                temp |= 0b10000000;
            }
            out.writeByte(temp);
        } while (paramInt != 0);
    }

    public static String readVarString(DataInputStream in, int size) throws IOException {
        String result = "";
        for (int i = 0; i < size; i++) {
            result += (char) in.readByte();
        }
        return result;
    }

    // Chunk data

    public static void writeVarString(DataOutputStream out, String string) throws IOException {
        writeVarInt(out, string.length());
        out.writeUTF(string);
    }

    public static void writePosition(DataOutputStream out, int x, int y, int z) throws IOException {
        out.writeLong(((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF));
    }

    public static byte[] getVarInt(int paramInt) throws IOException {
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

    public static byte[] getInt(int paramInt) throws IOException {
        return ByteBuffer.allocate(Integer.SIZE / 8).putInt(paramInt).array();
    }

    public static byte[] getLong(long paramLong) throws IOException {
        return ByteBuffer.allocate(Long.SIZE / 8).putLong(paramLong).array();
    }

    public static byte[] packString(String string) throws IOException {
        byte[] str = string.getBytes("UTF-8");
        byte[] len = getVarInt(str.length);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(len);
        result.write(str);
        return result.toByteArray();
    }

    public static void writeChunkDataPacket(Chunk chunk, DataOutputStream data) throws IOException {
        data.writeInt(chunk.getX());
        data.writeInt(chunk.getZ());
        data.writeBoolean(true);

        int mask = 0;
        ByteArrayOutputStream columnBufferByteArray = new ByteArrayOutputStream();
        DataOutputStream columnBuffer = new DataOutputStream(columnBufferByteArray);
        for (int sectionY = 0; sectionY < CHUNK_HEIGHT / SECTION_HEIGHT; sectionY++) {
            if (!chunk.isSectionEmpty(sectionY)) {
                mask |= (1 << sectionY);  // Set that bit to true in the mask
                writeChunkSection(chunk.getSection(sectionY), columnBuffer);
            }
        }
        for (int z = 0; z < SECTION_WIDTH; z++) {
            for (int x = 0; x < SECTION_WIDTH; x++) {
                columnBuffer.writeByte(chunk.getBiome(x, z).getId());  // Use 127 for 'void' if your server doesn't support biomes
            }
        }

        writeVarInt(data, mask);
        writeVarInt(data, columnBuffer.size());
        data.write(columnBufferByteArray.toByteArray());

        // If you don't support block entities yet, use 0
        // If you need to implement it by sending block entities later with the update block entity packet,
        // do it that way and send 0 as well.  (Note that 1.10.1 (not 1.10 or 1.10.2) will not accept that)

        /*writeVarInt(data, chunk.BlockEntities.Length);
        foreach(CompoundTag tag in chunk.BlockEntities) {
            WriteCompoundTag(data, tag);
        }*/
        writeVarInt(data, 0);
    }

    private static void writeChunkSection(Chunk.ChunkSection section, DataOutputStream data) throws IOException {
        byte bitsPerBlock = FULL_SIZE_BITS_PER_BLOCK;  // 13

        data.writeByte(bitsPerBlock);

        writeVarInt(data, 0);  // Palette size is 0

        // A bitmask that contains bitsPerBlock set bits
        //int individualValueMask = ((1 << bitsPerBlock) - 1);

        List<Long> blockData = new ArrayList<>();
        StringBuilder currentLong = new StringBuilder();
        BlockState currentState = null;
        for (int y = 0; y < SECTION_HEIGHT; y++) {
            for (int z = 0; z < SECTION_WIDTH; z++) {
                for (int x = 0; x < SECTION_WIDTH; x++) {
                    currentState = section.getState(x, y, z);
                    for (int i = 0; i < 4; i++) {
                        currentLong.insert(0, getBit(currentState.getMetadata(), i));
                        if (currentLong.length() == 64) {
                            blockData.add(parseLong(currentLong.toString()));
                            currentLong = new StringBuilder();
                        }
                    }
                    for (int i = 0; i < 9; i++) {
                        currentLong.insert(0, getBit(currentState.getId(), i));
                        if (currentLong.length() == 64) {
                            blockData.add(parseLong(currentLong.toString()));
                            currentLong = new StringBuilder();
                        }
                    }
                }
            }
        }

        writeVarInt(data, blockData.size());
        for (Long l : blockData)
            data.writeLong(l);

        for (int y = 0; y < SECTION_HEIGHT; y++) {
            for (int z = 0; z < SECTION_WIDTH; z++) {
                for (int x = 0; x < SECTION_WIDTH; x += 2) {
                    // Note: x += 2 above; we read 2 values along x each time
                    int value = section.getBlockLight(x, y, z) | (section.getBlockLight(x + 1, y, z) << 4);
                    data.writeByte(value);
                }
            }
        }

        if (section.getWorld().hasSkylight()) { // IE, current dimension is overworld / 0
            for (int y = 0; y < SECTION_HEIGHT; y++) {
                for (int z = 0; z < SECTION_WIDTH; z++) {
                    for (int x = 0; x < SECTION_WIDTH; x += 2) {
                        // Note: x += 2 above; we read 2 values along x each time
                        int value = section.getSkyLight(x, y, z) | (section.getSkyLight(x + 1, y, z) << 4);
                        data.writeByte(value);
                    }
                }
            }
        }
    }

    private static long parseLong(String s) {
        return new BigInteger(s, 2).longValue();
    }

    private static int getBit(int n, int k) {
        return (n >> k) & 1;
    }

}
