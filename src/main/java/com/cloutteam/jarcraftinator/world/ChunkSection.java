package com.cloutteam.jarcraftinator.world;

public class ChunkSection {
    private final int y;
    private BlockState[][][] blocks = new BlockState[16][16][16];

    public ChunkSection(int y, BlockState[][][] blocks) {
        this.y = y;
        this.blocks = blocks;
    }

    public int getY() {
        return y;
    }

    public boolean isEmpty() {
        return false;
    }

    public BlockState getState(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public int getBlockLight(int x, int y, int z) {
        return 15;
    }

    public int getSkyLight(int x, int y, int z) {
        return 15;
    }

    public boolean hasSkylight() {
        //TODO
        return true;
    }
}
