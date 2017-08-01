package com.cloutteam.jarcraftinator.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.api.*;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;
import com.cloutteam.jarcraftinator.protocol.packet.*;
import com.cloutteam.jarcraftinator.utils.UUIDManager;
import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class PlayerConnection extends Thread {

    private final Socket socket;

    private PacketHandshakeIn.NextState handshakeState = PacketHandshakeIn.NextState.NONE;
    private String username = "";

    public PlayerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (!socket.isClosed()) {
                try {
                    int packetLength = VarData.readVarInt(in);
                    int packetId = VarData.readVarInt(in);
                    switch (packetId) {
                        case 0x00:
                            switch (handshakeState) {
                                case STATUS:
                                    // Send the response back to the client
                                    new PacketStatusOutResponse(MinecraftVersion.v1_12, JARCraftinator.getConfig().getInt("max-players"), 0, null, ChatColor.translateAlternateColorCodes(JARCraftinator.getConfig().getString("pinger.motd")), JARCraftinator.getConfig().getString("pinger.favicon")).send(out);
                                    break;
                                case LOGIN:
                                    PacketLoginInLoginStart login = new PacketLoginInLoginStart();
                                    login.onReceive(packetLength, in);
                                    username = login.getPlayerName();

                                    UUID uuid = UUIDManager.getUUID(username);
                                    new PacketLoginOutLoginSuccess(uuid, username).send(out);
                                    new PacketPlayOutJoinGame(JARCraftinator.getNextEntityID(), GameMode.SURVIVAL, DimensionType.OVERWORLD, Difficulty.PEACEFUL, 10, LevelType.DEFAULT, false).send(out);
                                    break;
                                case NONE:
                                    // Receive the handshake and set the status
                                    PacketHandshakeIn handshake = new PacketHandshakeIn();
                                    handshake.onReceive(packetLength, in);
                                    handshakeState = handshake.getNextState();
                                    break;
                            }
                            break;
                        case 0x01:
                            PacketStatusInPing ping = new PacketStatusInPing();
                            ping.onReceive(packetLength, in);
                            new PacketStatusOutPong(ping.getLength(), ping.getData()).send(out);
                            socket.close();
                            break;
                    }
                } catch (EOFException e) {
                    JARCraftinator.err("Error while receiving packet from " + socket.getInetAddress().toString() + "!", "Closing connection!");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
