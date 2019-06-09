package com.cloutteam.jarcraftinator.entity.player;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.api.chat.ChatColor;
import com.cloutteam.jarcraftinator.api.json.JSONObject;
import com.cloutteam.jarcraftinator.exceptions.IOWriteException;
import com.cloutteam.jarcraftinator.handler.ConnectionHandler;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.protocol.ConnectionState;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;
import com.cloutteam.jarcraftinator.protocol.packet.*;
import com.cloutteam.jarcraftinator.utils.UUIDManager;
import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.Chunk;
import com.cloutteam.jarcraftinator.world.Difficulty;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.LevelType;
import com.cloutteam.jarcraftinator.world.navigation.Teleport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

public class PlayerConnection extends Thread {

    private final Socket socket;

    private ConnectionState connectionState = ConnectionState.HANDSHAKE;
    private boolean loggedIn = false;
    private Player player;

    private DataInputStream in;
    private DataOutputStream out;

    public PlayerConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }catch(IOException ex){
            JARCraftinator.getLogger().log("Unable to initiate two-way contact with " + socket.getInetAddress()
             + ":" + socket.getPort() + ". (" + ex.getMessage() + ")!", LogLevel.ERROR);

            try {
                if(!socket.isClosed()) {
                    socket.close();
                }
            }catch(IOException exception){
                JARCraftinator.getLogger().log("Additionally, an error occured whilst trying to close the " +
                    "connection. (" + exception.getMessage() + ")", LogLevel.ERROR);
            }

            interrupt();
            return;
        }

        while (!socket.isClosed()) {
            try {
                int packetLength = VarData.readVarInt(in);
                int packetId = VarData.readVarInt(in);

                switch (connectionState) {
                    case HANDSHAKE:
                        switch (packetId) {
                            case 0x00:
                                try {
                                    // Receive the handshake and set the next status
                                    PacketHandshakeIn handshake = new PacketHandshakeIn();
                                    handshake.onReceive(packetLength, in);
                                    connectionState = handshake.getNextState() == PacketHandshakeIn.NextState.LOGIN ? ConnectionState.LOGIN : ConnectionState.STATUS;
                                }catch(IOException | IOWriteException ex){
                                    JARCraftinator.getLogger().log("Failed to handshake with " +
                                            socket.getInetAddress() + ":" + socket.getPort(), LogLevel.ERROR);
                                    JARCraftinator.getLogger().log(ex.getMessage(), LogLevel.DEBUG);
                                }
                                break;
                            case 0xFE:
                                // TODO legacy ping
                                break;
                        }
                        break;
                    case STATUS:
                        switch (packetId) {
                            case 0x00:
                                // Empty packet
                                // Send the response back to the client
                                new PacketStatusOutResponse(
                                        JARCraftinator.protocolVersion,
                                        JARCraftinator.getConfig().getMaxPlayers(),
                                        0,
                                        null,
                                        ChatColor.translateAlternateColorCodes(JARCraftinator.getConfig().getMotd()),
                                        JARCraftinator.getConfig().getFavicon()).send(out);

                                JARCraftinator.getLogger().log(socket.getInetAddress() + ":" + socket.getPort() + " has pinged the server.");
                                break;
                            case 0x01:
                                try {
                                    PacketStatusInPing ping = new PacketStatusInPing();
                                    ping.onReceive(packetLength, in);
                                    new PacketStatusOutPong(ping.getLength(), ping.getData()).send(out);
                                    socket.close();
                                }catch(IOException ex){
                                    JARCraftinator.getLogger().log("Error whilst handling server ping (" +
                                        ex.getMessage() + ")", LogLevel.DEBUG);
                                }
                                break;
                        }
                        break;
                    case LOGIN:
                        switch (packetId) {
                            case 0x00:
                                String username;

                                try {
                                    PacketLoginInLoginStart login = new PacketLoginInLoginStart();
                                    login.onReceive(packetLength, in);
                                    username = login.getPlayerName();
                                }catch(IOException ex){
                                    JARCraftinator.getLogger().log("Error whilst handling player login (" +
                                            ex.getMessage() + ")", LogLevel.DEBUG);
                                    break;
                                }

                                UUID uuid = UUIDManager.getUUID(username);

                                player = new Player(username, uuid, this);
                                player.loadFromStorage();
                                JARCraftinator.getPlayerManager().addOnline(player);
                                new PacketLoginOutLoginSuccess(uuid, username).send(out);
                                connectionState = ConnectionState.PLAY;
                                new PacketPlayOutJoinGame(player.getEntityId(), GameMode.CREATIVE, DimensionType.OVERWORLD, Difficulty.PEACEFUL, 10, LevelType.DEFAULT, false).send(out);
                                PacketPlayOutSpawnPosition spawnPacket = new PacketPlayOutSpawnPosition(0, 64, 0);
                                spawnPacket.send(out);
                                JARCraftinator.getLogger().log("Player " + username + " has logged in from " + socket.getInetAddress() + ":" + socket.getPort() + " with UUID " + uuid.toString() + ".");
                                JARCraftinator.getLogger().log(username + " has spawned on the server at (" + spawnPacket.getX() + ", " + spawnPacket.getY() + ", " + spawnPacket.getZ() + ").");
                                break;
                            case 0x01:
                                // TODO encryption response
                                break;
                        }
                        break;
                    case PLAY:
                        switch (packetId) {
                            case 0x00:
                                try {
                                    PacketPlayInTeleportConfirm teleportConfirm = new PacketPlayInTeleportConfirm();
                                    teleportConfirm.onReceive(packetLength, in);
                                    if (JARCraftinator.getTeleportManager().confirmTeleport(teleportConfirm.getTeleportID()))
                                        JARCraftinator.getLogger().log("Confirmed teleport: " + teleportConfirm.getTeleportID(), LogLevel.DEBUG);
                                    else
                                        JARCraftinator.getLogger().log("Failed to confirm teleport " + teleportConfirm.getTeleportID() + " because it's either already confirmed or it doesn't exist.", LogLevel.DEBUG);
                                }catch (IOException ex){
                                    JARCraftinator.getLogger().log("Error whilst confirming teleportation (" +
                                            ex.getMessage() + ")", LogLevel.DEBUG);
                                }
                                break;
                            case 0x02:
                                try {
                                    PacketPlayInChat chatPacket = new PacketPlayInChat();
                                    chatPacket.onReceive(packetLength, in);

                                    if(!chatPacket.isValid()){
                                        //TODO: Kick
                                        JARCraftinator.getLogger().log(player.getName() + " sent a message of length greater than 256 characters. This is not normally possible.", LogLevel.WARNING);
                                        socket.close();
                                    }

                                    if(chatPacket.getMessage().startsWith("/")){
                                        return;
                                    }

                                    String message = getPlayer().getName() + " > " + chatPacket.getMessage();

                                    JSONObject chatComponent = new JSONObject();
                                    chatComponent.add("text", message);
                                    PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatComponent.toString());

                                    for(PlayerConnection connection :
                                            JARCraftinator.getConnectionHandler().getAllPlayerConnections()){
                                        packetPlayOutChat.send(connection.getOut());
                                    }
                                }catch(IOException ex){
                                    JARCraftinator.getLogger().log("Error whilst receiving message (" +
                                            ex.getMessage() + ")", LogLevel.DEBUG);
                                }
                                break;
                            case 0x04:
                                try {
                                    PacketPlayInClientSettings clientSettings = new PacketPlayInClientSettings();
                                    clientSettings.onReceive(packetLength, in);
                                    if (loggedIn)
                                        break;
                                    loggedIn = true;
                                    JARCraftinator.getLogger().log("Player's locale: " + clientSettings.getLocale(), LogLevel.DEBUG);
                                    new PacketPlayOutPlayerPositionAndLook(0, 128, 0, 0, 0, (byte) 0, new Teleport(player, null, player.getLocation(), Teleport.TeleportCause.LOGIN).getId()).send(out);
                                    int chunkX = (int) Math.floor(player.getLocation().getX() / 16);
                                    int chunkZ = (int) Math.floor(player.getLocation().getZ() / 16);
                                    for (int x = chunkX - clientSettings.getViewDistance(); x < chunkX + clientSettings.getViewDistance(); x++)
                                        for (int z = chunkZ - clientSettings.getViewDistance(); z < chunkZ + clientSettings.getViewDistance(); z++)
                                            new PacketPlayOutChunkData(new Chunk(player.getLocation().getWorld(), x, z)).send(out);

                                    // Start the KeepAlive runnable!
                                    new PlayerKeepAlive(this);
                                }catch(IOException ex){
                                    JARCraftinator.getLogger().log("Error whilst handling client settings packet (" +
                                            ex.getMessage() + ")", LogLevel.DEBUG);
                                }
                                break;
                            case 0x0B:
                                PacketPlayInKeepAlive packetPlayInKeepAlive = new PacketPlayInKeepAlive();
                                packetPlayInKeepAlive.onReceive(packetLength, in);
                                break;
                            case 0x0E:
                                try {
                                    PacketPlayInPlayerPositionAndLook packetPlayInPlayerPositionAndLook = new PacketPlayInPlayerPositionAndLook();
                                    packetPlayInPlayerPositionAndLook.onReceive(packetLength, in);
                                    JARCraftinator.getLogger().log("X: " + packetPlayInPlayerPositionAndLook.getX(), LogLevel.DEBUG);
                                    JARCraftinator.getLogger().log("Y: " + packetPlayInPlayerPositionAndLook.getY(), LogLevel.DEBUG);
                                    JARCraftinator.getLogger().log("Z: " + packetPlayInPlayerPositionAndLook.getZ(), LogLevel.DEBUG);
                                }catch(IOException ex){
                                    JARCraftinator.getLogger().log("Error whilst handling player move packet (" +
                                        ex.getMessage() + ")", LogLevel.DEBUG);
                                }
                                break;
                            default:
                                // JARCraftinator.getLogger().log("Unknown packet ID: " + Integer.toHexString(packetId), LogLevel.DEBUG);
                                try {
                                    for (packetLength -= VarData.getVarInt(packetId).length; packetLength > 0; packetLength--)
                                        in.readByte();
                                }catch(IOException ex){
                                    ex.printStackTrace();
                                }

                        }
                        break;
                }
            } catch (EOFException | SocketException e) {
                JARCraftinator.getLogger().log("Error while receiving packet from " + socket.getInetAddress().toString() + "! Ignoring...", LogLevel.DEBUG);
                break;
            } catch (IOException ex){
                if(socket.isClosed()){
                    break;
                }
            }
        }

        if (loggedIn) {
            JARCraftinator.getLogger().log(getPlayer().getName() + " has quit the server!");
            loggedIn = false;
        }

        interrupt();
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public Player getPlayer() {
        return player;
    }

    boolean isLoggedIn() {
        return loggedIn;
    }
}
