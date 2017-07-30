package com.cloutteam.jarcraftinator.utils;

import java.io.DataInputStream;
import java.io.IOException;

public class VarData {

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

    public static String readVarString(DataInputStream in, int size) throws IOException {
        String result = "";
        for(int i = 0; i < size; i++){
            result += (char) in.readByte();
        }
        return result;
    }

}
