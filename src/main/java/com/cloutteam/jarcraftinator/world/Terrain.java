package com.cloutteam.jarcraftinator.world;
import java.util.Random;

public class Terrain {
    private Random rand_ = new Random();
    private final int height;
    private int[][] map = new int[16][16];

    public Terrain(int height) {
        this.height = height;
    }

    public void generate() {
        for (int x=0; x<16; x++) {
            for (int z=0; z<16; z++) {
                map[x][z] = rand_.nextInt(10);
            }
        }
    }

    public BlockState getBlock(int x, int y, int z) {
        if (y < map[x][z]) {
            return new BlockState(1, 1);
        } else if (y == map[x][z]) {
            return new BlockState(2, 0);
        }
        return new BlockState(0, 0);
    }
}

                

