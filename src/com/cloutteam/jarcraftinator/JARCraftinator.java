package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JARCraftinator {

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(25565);
            Socket clientSocket = serverSocket.accept();
            DataInputStream stream = new DataInputStream(clientSocket.getInputStream());

            VarData.readVarInt(stream);
            VarData.readVarInt(stream);

            // VARINT - Protocol Version
            System.out.println(VarData.readVarInt(stream));
            // STRING - Server address
            System.out.println(VarData.readVarString(stream, VarData.readVarInt(stream)));
            // USHORT - Server port
            System.out.println(stream.readUnsignedShort());
            // VARINT - Next state
            System.out.println(VarData.readVarInt(stream));

            clientSocket.close();
            serverSocket.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
