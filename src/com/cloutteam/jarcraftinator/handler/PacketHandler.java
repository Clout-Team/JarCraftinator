package com.cloutteam.jarcraftinator.handler;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.api.cloutteam.samjakob.ChatColor;
import com.cloutteam.jarcraftinator.utils.QuickJSON;
import com.cloutteam.jarcraftinator.utils.VarData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class PacketHandler extends Thread {

    private Socket clientSocket;
    private boolean connected;
    private int state;
    private byte[] privateKey;
    private String playerName;

    public PacketHandler(Socket socket){
        clientSocket = socket;
        connected = true;
    }

    @Override
    public void run() {

        DataInputStream stream = null;
        DataOutputStream output = null;

        try {
            stream = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        while (!currentThread().isInterrupted() && !isDisconnected()) {
            try {
                while (stream.available() > 0) {
                    int packetLength = VarData.readVarInt(stream);
                    int packetID = VarData.readVarInt(stream);
                    boolean wasHandled = false;

                    JARCraftinator.log("Packet length: " + packetLength);
                    JARCraftinator.log("Packet ID: " + packetID);

                    /* HANDSHAKE */
                    if (packetID == PacketType.HANDSHAKE_PACKET && packetLength > 1 && state < 2) {
                        JARCraftinator.log("Handshake from: ", JARCraftinator.getIPAddress(clientSocket));
                        // VARINT - Protocol Version
                        JARCraftinator.log("Protocol Version: " + VarData.readVarInt(stream));
                        // STRING - Server address (size)
                        JARCraftinator.log("Server Address: " + VarData.readVarString(stream, VarData.readVarInt(stream)));
                        // USHORT - Server port
                        JARCraftinator.log("Server Port: " + stream.readUnsignedShort());
                        // VARINT - Next state
                        state = VarData.readVarInt(stream);
                        JARCraftinator.log("Next State: " + state);
                        wasHandled = true;
                    }

                    /* SLP */
                    if (packetID == PacketType.REQUEST_PACKET && packetLength == 1) {
                        // Send SLP response
                        JARCraftinator.log("Server pinged by: ", JARCraftinator.getIPAddress(clientSocket));

                        JSONObject slp = new JSONObject();
                        slp.put("version", QuickJSON.getVersionMap("1.12", 335));
                        slp.put("players", QuickJSON.players(0, JARCraftinator.getConfig().getInt("max-players")));
                        slp.put("description", QuickJSON.description(ChatColor.translateAlternateColorCodes(JARCraftinator.getConfig().getString("pinger.motd"))));
                        slp.put("favicon", JARCraftinator.getConfig().getString("pinger.favicon"));

                        // Write total packet length (JSON String length, String length byte length, packet ID length)
                        byte[] pid = VarData.getVarInt(0);
                        byte[] packet = VarData.packString(slp.toJSONString());

                        // Write packet length (packet ID length and packet data length)
                        output.write(VarData.getVarInt(pid.length + packet.length));
                        // Write packet ID
                        output.write(pid);
                        // Write payload
                        output.write(packet);
                        // Flush output
                        output.flush();
                        JARCraftinator.log("Server list ping complete!");

                        if (clientSocket.isConnected()) {
                            JARCraftinator.log("Client still connected so waiting for ping packet...");

                            // Ping packet //
                            VarData.readVarInt(stream); // Packet length
                            VarData.readVarInt(stream); // Packet ID
                            long payload = stream.readLong(); // Packet payload
                            JARCraftinator.log("Ping packet payload: " + payload);

                            JARCraftinator.log("Sending pong packet...");
                            // Pong packet //
                            pid = VarData.getVarInt(0x01);
                            VarData.writeVarInt(output, pid.length + 8);
                            output.write(pid);
                            output.writeLong(payload);
                            output.flush();
                            JARCraftinator.log("Sent.");
                        }

                        wasHandled = true;
                        disconnect();
                        clientSocket.close();
                        JARCraftinator.endConnection(this);
                    }

                    /* LOGIN START */
                    if(packetID == PacketType.LOGIN_START && state == 2){
                        playerName = VarData.readVarString(stream, VarData.readVarInt(stream));
                        JARCraftinator.log("Player attempting login: " + playerName);

                        // Generate encryption data
                        byte[] serverid = VarData.packString("");
                        byte[] publickey = getPublicKey();
                        byte[] publicKeyLength = VarData.getVarInt(publickey.length);
                        byte[] verifyTokenLength = VarData.getVarInt(4);
                        byte[] verifyToken = new byte[4];
                        verifyToken[0] = 6;
                        verifyToken[1] = 9;
                        verifyToken[2] = 6;
                        verifyToken[3] = 9;
                        // Packet headers
                        VarData.writeVarInt(output, VarData.getVarInt(0x01).length + serverid.length + publicKeyLength.length + publickey.length + verifyTokenLength.length + verifyToken.length);
                        VarData.writeVarInt(output, 0x01);
                        // Write packet data
                        output.write(serverid);
                        output.write(publicKeyLength);
                        output.write(publickey);
                        output.write(verifyTokenLength);
                        output.write(verifyToken);

                        wasHandled = true;
                    }

                    /* ENCRYPTION RESPONSE */
                    if(packetID == PacketType.ENCRYPTION_RESPONSE && state == 2){
                        JARCraftinator.log("Recieved encryption response from " + playerName);
                        int sslength = VarData.readVarInt(stream);
                        byte[] ss = new byte[sslength];
                        stream.read(ss, 0, sslength);

                        int vtlength = VarData.readVarInt(stream);
                        byte[] vt = new byte[vtlength];
                        stream.read(vt, 0, vtlength);

                        /* NOW RESPOND WITH LOGIN SUCCESS */
                        byte[] uuid = VarData.packString(getUUID(playerName));
                        byte[] username = VarData.packString(playerName);

                        VarData.writeVarInt(output, VarData.getVarInt(0x02).length + uuid.length + username.length);
                        VarData.writeVarInt(output, 0x02);
                        output.write(uuid);
                        output.write(username);

                        state = 3;

                        /* SEND JOIN GAME PACKET */
                        byte[] biome = VarData.packString("default");
                        VarData.writeVarInt(output, 12 + biome.length);
                        VarData.writeVarInt(output,0x23);
                        output.writeInt(6969); // size: 4
                        output.write(0x1); // size: 1
                        output.writeInt(0); // size: 4
                        output.write(0x0); // size: 1
                        output.write((byte) 10); //size: 1
                        output.write(biome);
                        output.write(0x00); //size: 1

                        System.out.println("debug1");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        /* SEND HOME POSITION */
                        int x = 0;
                        int y = 5;
                        int z = 0;
                        long position = ((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF);
                        VarData.writeVarInt(output, VarData.getVarInt(0x45).length + Math.round(Long.bitCount(position) / 8));
                        VarData.writeVarInt(output, 0x45);
                        output.writeLong(position);
                        output.writeLong(y);
                        output.writeLong(z);

                        System.out.println("debug2");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        /* SEND PLAYER ABILITIES */
                        VarData.writeVarInt(output, 9 + VarData.getVarInt(0x2B).length);
                        VarData.writeVarInt(output, 0x2B);
                        output.writeByte(0x08);
                        output.writeFloat(1);
                        output.writeFloat(0);

                        wasHandled = true;
                    }

                    if(packetID == PacketType.CLIENT_SETTINGS && state == 3){
                        String locale = VarData.readVarString(stream, VarData.readVarInt(stream));
                        int viewDistance = stream.readByte();
                        int chatMode = VarData.readVarInt(stream);
                        boolean colors = stream.readBoolean();
                        int displayedSkinParts = stream.readUnsignedByte();
                        int mainHand = VarData.readVarInt(stream);

                        JARCraftinator.log("Locale: " + locale);
                        JARCraftinator.log("Render Distance: " + viewDistance);
                        JARCraftinator.log("Chat Mode: " + chatMode);
                        JARCraftinator.log("Colors enabled: " + colors);
                        JARCraftinator.log("Displayed Skin Parts: " + displayedSkinParts);
                        JARCraftinator.log("Main hand [0=left,1=right]: " + mainHand);
                    }

                    if(!wasHandled) {
                        System.out.println("Unknown packet type recieved from: " + JARCraftinator.getIPAddress(clientSocket) + ", type=" + Byte.toString((byte) packetID) + ",length=" + packetLength);
                    }
                }
            }catch(IOException ex){
                disconnect();
            }
        }
    }

    public boolean isDisconnected(){
        return !connected;
    }

    public void disconnect(){
        connected = false;
    }

    private byte[] getPublicKey(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(512);
            KeyPair keys = generator.generateKeyPair();
            privateKey = keys.getPrivate().getEncoded();
            return keys.getPublic().getEncoded();
        }catch(NoSuchAlgorithmException ex){
            System.out.println("Error generating RSA key for player.");
            return null;
        }
    }

    private String getUUID(String name){
        String USER_AGENT = "CLOUT-JARCRAFTINATOR/1.0";
        String api_url = "http://api.clout-team.com/minecraft/?name=" + name;

        JARCraftinator.log("Unable to get UUID for player " + playerName + " - generating a random one instead.");
        return UUID.randomUUID().toString();
    }

}

class PacketType {

    public static final byte HANDSHAKE_PACKET = 0x00;
    public static final byte REQUEST_PACKET = 0x00;
    public static final byte LOGIN_START = 0x00;
    public static final byte ENCRYPTION_RESPONSE = 0x01;

    public static final byte CLIENT_SETTINGS = 0x05;

}

