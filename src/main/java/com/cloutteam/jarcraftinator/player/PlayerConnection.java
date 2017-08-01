package com.cloutteam.jarcraftinator.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.api.ChatColor;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;
import com.cloutteam.jarcraftinator.protocol.packet.PacketHandshakeIn;
import com.cloutteam.jarcraftinator.protocol.packet.PacketStatusInPing;
import com.cloutteam.jarcraftinator.protocol.packet.PacketStatusOutPong;
import com.cloutteam.jarcraftinator.protocol.packet.PacketStatusOutResponse;
import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PlayerConnection extends Thread {

    private final Socket socket;
    private PacketHandshakeIn.NextState handshakeState = PacketHandshakeIn.NextState.NONE;

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
                                    // TODO do the login system
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
