package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.world.BlockState;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        try {
            BlockState block = new BlockState();

            int BITS_PER_BLOCK = 13;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            long currentLong = 0;
            int bitsWritten = 0;
            for (int y = 0; y < 16; y++)
                for (int z = 0; z < 16; z++)
                    for (int x = 0; x < 16; x++) {
                        if (bitsWritten + BITS_PER_BLOCK <= 64) {
                            currentLong |= (((long) (block.getId() << 4 | block.getMetadata())) << bitsWritten);
                            bitsWritten += BITS_PER_BLOCK;
                        } else {
                            int newLong = BITS_PER_BLOCK - 64 + bitsWritten;
                            currentLong |= (((long) (block.getId() << 4 | block.getMetadata())) << bitsWritten);
                            stream.write(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(currentLong).array());
                            currentLong = 0;
                            currentLong |= ((long) (block.getId() << 4 | block.getMetadata())) >> BITS_PER_BLOCK - newLong;
                            bitsWritten = newLong;
                        }
                        if (bitsWritten == 64) {
                            stream.write(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(currentLong).array());
                            currentLong = 0;
                            bitsWritten = 0;
                        }
                    }

            System.out.println(Arrays.toString(stream.toByteArray()));
            System.out.println(stream.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
