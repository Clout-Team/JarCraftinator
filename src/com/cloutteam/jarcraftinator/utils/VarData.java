package com.cloutteam.jarcraftinator.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static String readVarString(DataInputStream in, int size) throws IOException {
        String result = "";
        for(int i = 0; i < size; i++){
            result += (char) in.readByte();
        }
        return result;
    }

    public static void writeVarString(DataOutputStream out, String string) throws IOException {
        writeVarInt(out, string.length());
        out.writeUTF(string);
    }

    public static byte[] getVarInt(int paramInt) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write((byte)paramInt);
                return out.toByteArray();
            }

            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static byte[] packString(String string) throws IOException {
        byte[] str = string.getBytes("UTF-8");
        byte[] len = getVarInt(str.length);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(len);
        result.write(str);
        return result.toByteArray();
    }

}
