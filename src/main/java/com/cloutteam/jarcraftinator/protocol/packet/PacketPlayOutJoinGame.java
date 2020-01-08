package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.entity.player.GameMode;
import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.Difficulty;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.LevelType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
        
        // 1.15.1
        // EXAMPLE SEED: -962639596097062
        // Hash: 2c9b9c694154884c7e71292b61248b4d6bf88322bf1e6236d889844afea1b211
        String hash = "2c9b9c694154884c7e71292b61248b4d6bf88322bf1e6236d889844afea1b211";
        //byte[] levelHash = hash.getBytes();
        byte[] bytes = {32, 63, 39, 62, 39, 63, 36, 39};
        
        
        
        long test = bytesToLong(bytes);
        System.out.println("Bytes: "+test);
        
        VarData.writeVarInt(out, levelType.length + packetId.length+ bytes.length+13);
        out.write(packetId);
        
        out.writeInt(entityID);
        out.writeByte(gamemode.getId());
        out.writeInt(dimensionType.getId());
        


        out.writeLong(test);
        //out.writeLong(12345678);
        
        // Not needed in 1.15.1
        //out.writeByte(difficulty.getId());
        out.writeByte(maxPlayers);
        out.write(levelType);
        out.write(16);
        out.writeBoolean(false);
        out.writeBoolean(false);
        out.flush();
        System.out.println("PacketPlayOutJoinGame");
    }
    
    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
    
    
}
