package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.entity.player.GameMode;
import com.cloutteam.jarcraftinator.world.Difficulty;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.LevelType;

import java.io.IOException;

public class PacketPlayOutJoinGame extends PacketOut {

    private int entityID;
    private GameMode gamemode;
    private DimensionType dimensionType;
    private Difficulty difficulty;
    private int maxPlayers;
    private LevelType levelType;
    private boolean reducedDebug;

    public PacketPlayOutJoinGame(int entityID, GameMode gamemode, DimensionType dimensionType, Difficulty difficulty, int maxPlayers, LevelType levelType, boolean reducedDebug) {
        this.entityID = entityID;
        this.gamemode = gamemode;
        this.dimensionType = dimensionType;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        this.reducedDebug = reducedDebug;
    }

    @Override
    public void send(PacketSerializer serializer) throws IOException {
        serializer.withPacketId(0x23).writeInt(entityID).writeBytes((byte) gamemode.getId()).writeInt(dimensionType.getId()).writeBytes((byte) difficulty.getId()).writeBytes((byte) maxPlayers).writeString(this.levelType.getId()).writeBoolean(reducedDebug);
    }
}
