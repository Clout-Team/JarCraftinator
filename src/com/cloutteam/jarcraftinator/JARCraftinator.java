package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.utils.QuickJSON;
import com.cloutteam.jarcraftinator.utils.VarData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import sun.corba.OutputStreamFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class JARCraftinator {

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(25565);
            Socket clientSocket = serverSocket.accept();
            DataInputStream stream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            /* HANDSHAKING */

            System.out.println("HANDSHAKING:");
            // Packet (ID+data) length
            System.out.println("Length: " + VarData.readVarInt(stream));
            // Packet ID
            System.out.println("Packet ID: " + VarData.readVarInt(stream));

            System.out.println("Payload_");

            // VARINT - Protocol Version
            System.out.println("Protocol Version: " + VarData.readVarInt(stream));
            // STRING - Server address (size)
            System.out.println("Server Address: " + VarData.readVarString(stream, VarData.readVarInt(stream)));
            // USHORT - Server port
            System.out.println("Server Port: " + stream.readUnsignedShort());
            // VARINT - Next state
            System.out.println("Next State: " + VarData.readVarInt(stream));

            // Request packet //
            // Read packet ID (empty packet)
            System.out.println();
            System.out.println("REQUEST PACKET:");
            System.out.println("Length: " + VarData.readVarInt(stream));
            System.out.println("Packet ID: " + VarData.readVarInt(stream));

            /* STATUS */
            JSONObject slp = new JSONObject();
            slp.put("version", QuickJSON.getVersionMap("1.12", 335));
            slp.put("players", QuickJSON.players(0, 100));
            slp.put("description", QuickJSON.description("Hello world"));

            System.out.println();

            // Write total packet length (JSON String length, String length byte length, packet ID length)
            byte[] pid = VarData.getVarInt(0);
            byte[] packet = VarData.packString(slp.toJSONString());

            //VarData.writeVarInt(output, packet.length + pid.length );
            output.write(VarData.getVarInt(packet.length + pid.length));
            // Write packet ID
            output.write(pid);
            // Write payload
            output.write(packet);
            // Flush output
            output.flush();

            // Ping packet //
            // Packet ID
            System.out.println("Should be 1:: " + VarData.readVarInt(stream));
            long data = stream.readLong();

            // Pong packet //


            clientSocket.close();
            serverSocket.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}