package com.cloutteam.jarcraftinator.world;
import java.util.Random;
import com.cloutteam.jarcraftinator.world.terrain.noise.Noise;
import com.cloutteam.jarcraftinator.world.terrain.noise.PerlinNoise;

public class Terrain {
    private final int WORLDSIZE 1024; //fixed worldsize for now
    private final int MAXHEIGHT 256;

    private final int height;
    private int[][] worldHeightMap = new int[WORLDSIZE][WORLDSIZE];
    private Noise noise;

    public Terrain(int height) {
        this.height = height;
        this.noise = new PerlinNoise(15, 0.05, 0.5);
    }

    public void generate() {
        //generate worldHmap
        noise.set(1, 16, 0.5);
        for (int x=0; x<WORLDSIZE; x++) {
            for (int z=0; z<WORLDSIZE; z++) {
                worldHeightMap[x][z] = (int) (MAXHEIGHT*noise.octavedNoise(x, z, 0));
            }
        }
    }

    public Chunk genChunk(int chunkX, int chunkZ) {
        int maxHeight = 40;
        noise.set(8, 0.5, 0.5);
        BlockState[16][16][16] blocks = new BlockState[16][16][16];
        for (int x=0; x<16; x++) {
            for (int y=0; y<16; y++) {
                for (int z=0; z<16; z++) {
                    blocks[x][y][z] = getBlock(x, y, z);
                }
            }
        }

        Chunk chunk = new Chunk(new World("dummy", DimensionType.OVERWORLD), chunkX, chunkZ);
        chunk.setSections(blocks);

        return chunk;
    }

    private BlockState getBlock(int x, int y, int z) {
        height = noise.octavedNoise(chunkX + x, chunkZ + z, 0) + worldHeightMap[x][z];
        if (y == height) return BlockState(2, 0);
        if (y < height) return BlockState(3, 0);
        if (y == 0) return BlockState(7, 0);
        return BlockState(0, 0);
    }

    public BlockState getBlock(int x, int y, int z) {
        if (y < map[x][z]) {
            return new BlockState(3, 0);
        } else if (y == map[x][z]) {
            return new BlockState(2, 0);
        }
        return new BlockState(0, 0);
    }

    public void generateBiomeMap() {
        return;
    }
}
