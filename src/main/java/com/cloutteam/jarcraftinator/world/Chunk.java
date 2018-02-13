package com.cloutteam.jarcraftinator.world;

public class Chunk {

    private final World world;
    private final int x;
    private final int z;

    private ChunkSection[] chunkSections = new ChunkSection[16];

    public Chunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean isSectionEmpty(int y) {
        //TODO check section array

        // For testing proposes only
        return y != 3;
    }

    public void setSections(ChunkSection[16] chunkSections) {
        this.chunkSections = chunkSections;
    }

    public World getWorld() {
        return world;
    }

    public ChunkSection getSection(int y) {
        return chunkSections[y];
    }

    public Biome getBiome(int x, int z) {
        // For testing proposes only
        return Biome.PLAINS;
    }

    public class ChunkSection {
        private final int y;
        private BlockState[][][] blocks = new BlockState[16][16][16];

        public ChunkSection(int y, BlockState[16][16][16] blocks) {
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

        public World getWorld() {
            return world;
        }

    }

}
