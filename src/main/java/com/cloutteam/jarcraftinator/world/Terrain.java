package com.cloutteam.jarcraftinator.world;
import java.util.Random;
import com.cloutteam.jarcraftinator.world.terrain.noise.Noise;
import com.cloutteam.jarcraftinator.world.terrain.noise.PerlinNoise;

public class Terrain {
    private final int height;
    private int[][] worldMap = new int[1024][1024];
    private Noise noise;

    public Terrain(int height) {
        this.height = height;
	this.noise = new PerlinNoise(15, 0.05, 0.5);
    }

    public void generate() {
        for (int x=0; x<16; x++) {
            for (int z=0; z<16; z++) {
                map[x][z] = (int) (maxHeight*noise.octavedNoise(x, z + chunkZ*16, 0));
            }
        }
    }

    public void genChunk(int X, int Z) {
	int maxHeight = 40;
        for (int x=0; x<16; x++) {
            for (int z=0; z<16; z++) {
                map[x][z] = (int) (maxHeight*noise.octavedNoise(x, z + chunkZ*16, 0));
            }
        }
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

                

