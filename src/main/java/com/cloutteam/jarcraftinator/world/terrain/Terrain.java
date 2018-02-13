package com.cloutteam.jarcraftinator.world.terrain;

import java.util.Random;
import com.cloutteam.jarcraftinator.world.terrain.noise.Noise;
import com.cloutteam.jarcraftinator.world.terrain.noise.PerlinNoise;
import com.cloutteam.jarcraftinator.world.Chunk;
import com.cloutteam.jarcraftinator.world.ChunkSection;
import com.cloutteam.jarcraftinator.world.BlockState;
import com.cloutteam.jarcraftinator.world.DimensionType;
import com.cloutteam.jarcraftinator.world.World;

public class Terrain {
    private final int WORLDSIZE =  1024; //fixed worldsize for now
    private final int MAXHEIGHT = 256;

    private double[][] worldHeightMap = new double[WORLDSIZE][WORLDSIZE];
    private Noise noise;

    public Terrain() {
        this.noise = new PerlinNoise(15, 0.05, 0.5);
    }

    public void generate() {
        //generate worldHmap
        noise.set(2, 0.001, 0.5);
        for (int x=0; x<WORLDSIZE; x++) {
            for (int z=0; z<WORLDSIZE; z++) {
                worldHeightMap[x][z] = noise.octavedNoise(x, z, 0);
            }
        }
    }

    public Chunk genChunk(int chunkX, int chunkZ) {
        long start = System.nanoTime();
        ChunkSection[] sections = new ChunkSection[16];
        
        for (int y=0; y<16; y++) {
            sections[y] = getSection(chunkX, y, chunkZ);
        }

        Chunk chunk = new Chunk(new World("dummy", DimensionType.OVERWORLD), chunkX, chunkZ);
        chunk.setSections(sections);

        long stop = System.nanoTime();

        System.out.printf("loaded chunk in %f seconds", (stop-start)/Math.pow(10, 9));

        return chunk;
    }

    private ChunkSection getSection(int chunkX, int chunkY, int chunkZ) {
        int maxHeight = 40;
        noise.set(4, 0.01, 0.8);
        BlockState[][][] blocks = new BlockState[16][16][16];
        for (int x=0; x<16; x++) {
            for (int y=0; y<16; y++) {
                for (int z=0; z<16; z++) {
                    blocks[x][y][z] = getBlock(x+chunkX*16, y+chunkY*16, z+chunkZ*16);
                }
            }
        }
        return new ChunkSection(chunkY, blocks);
    }

    private BlockState getBlock(int x, int y, int z) {
        int height = (int) ( MAXHEIGHT * noise.octavedNoise(x, z, y) * worldHeightMap[Math.abs((x + WORLDSIZE/2) % WORLDSIZE)][Math.abs((z + WORLDSIZE/2) % WORLDSIZE)] );
        if (height > 0.8) {
            return new BlockState(2, 0);
        }
        if (y < height) {
            return new BlockState(3, 0);
        }
        if (y == 0) {
            return new BlockState(7, 0);
        }
        return new BlockState(0, 0);
    }

    public void generateBiomeMap() {
        return;
    }
}
