package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.entity.player.GameMode;
import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.Difficulty;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.LevelType;

import java.io.DataOutputStream;
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
    public void send(DataOutputStream out) throws IOException {
        byte[] packetId = VarData.getVarInt(MinecraftPacket.PLAY.JOIN_GAME.out);
        byte[] levelType = VarData.packString(this.levelType.getId());
        VarData.writeVarInt(out, 12 + levelType.length + packetId.length);
        out.write(packetId);
        out.writeInt(entityID);
        out.writeByte(gamemode.getId());
        out.writeInt(dimensionType.getId());
        out.writeByte(difficulty.getId());
        out.writeByte(maxPlayers);
        out.write(levelType);
        out.writeBoolean(reducedDebug);
        out.flush();
    }
}
