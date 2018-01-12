package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.api.json.JSONObject;
import com.cloutteam.jarcraftinator.entity.player.Player;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;
import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PacketStatusOutResponse extends PacketOut {

    private MinecraftVersion version;
    private int maxPlayers;
    private int onlinePlayers;
    private List<Player> playerList;
    private String motd;
    private String favicon;

    /**
     * Creates a Status Response packet.
     *
     * @param version       The version the server is in.
     * @param maxPlayers    The maximum players allowed by the server.
     * @param onlinePlayers The online players amount.
     * @param playerList    The list of the online players. Can be null.
     * @param motd          The MOTD of the server.
     * @param favicon       The favicon of the server in base64. The correct format is "data:image/png;base64,<data>". MUST be a PNG image. Can be empty.
     */
    public PacketStatusOutResponse(MinecraftVersion version, int maxPlayers, int onlinePlayers, List<Player> playerList, String motd, String favicon) {
        this.version = version;
        this.maxPlayers = maxPlayers;
        this.onlinePlayers = onlinePlayers;
        this.playerList = playerList;
        this.motd = motd;
        this.favicon = favicon;
    }

    /**
     * The version the server is in.
     *
     * @return A @link{com.cloutteam.jarcraftinator.MinecraftVersion} object.
     */
    public MinecraftVersion getVersion() {
        return version;
    }

    /**
     * Sets the server version.
     *
     * @param version The version the server is in.
     */
    public void setVersion(MinecraftVersion version) {
        this.version = version;
    }

    /**
     * Gets the maximum players allowed by the server.
     *
     * @return The maximum players allowed by the server.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets the maximum players.
     *
     * @param maxPlayers The maximum players allowed by the server.
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Gets the online players.
     *
     * @return
     */
    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * Sets the online players.
     *
     * @param onlinePlayers The online players amount.
     */
    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    /**
     * Gets the online player list.
     *
     * @return A @link{java.util.List} containing all the @link{com.cloutteam.jarcraftinator.entity.player.Player Players}. Can be null.
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Sets the online player list.
     *
     * @param playerList The list of the online players. Can be null.
     */
    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    /**
     * Gets the MOTD of the server.
     *
     * @return The MOTD of the server.
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Sets the MOTD of the server.
     *
     * @param motd The MOTD of the server.
     */
    public void setMotd(String motd) {
        this.motd = motd;
    }

    /**
     * Gets the server favicon.
     *
     * @return The favicon of the server in base64. Can be empty.
     */
    public String getFavicon() {
        return favicon;
    }

    /**
     * Sets the server favicon.
     *
     * @param favicon The favicon of the server in base64. The correct format is "data:image/png;base64,&lt;data&gt;". MUST be a PNG image. Can be empty.
     */
    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
        JSONObject response = new JSONObject().add("version", new JSONObject().add("name", this.version.getName()).add("protocol", this.version.getProtocol())).add("players", new JSONObject().add("max", maxPlayers).add("online", onlinePlayers)).add("description", new JSONObject().add("text", motd));
        if (!favicon.isEmpty())
            response.add("favicon", favicon);
        String responseText = response.toString();

        //TODO sample object inside player

        byte[] packetId = VarData.getVarInt(0x00);
        byte[] dataLength = VarData.getVarInt(responseText.getBytes().length);
        VarData.writeVarInt(out, responseText.getBytes().length + packetId.length + dataLength.length);
        out.write(packetId);
        out.write(dataLength);
        out.writeBytes(responseText);
        out.flush();
    }
}
