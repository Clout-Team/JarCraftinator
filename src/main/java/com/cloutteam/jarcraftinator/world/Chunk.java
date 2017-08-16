package com.cloutteam.jarcraftinator.world;

public class Chunk {

    private final World world;
    private final int x;
    private final int z;

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

    public World getWorld() {
        return world;
    }

    public ChunkSection getSection(int y) {
        // Return dummy section
        return new ChunkSection(y);
    }

    public Biome getBiome(int x, int z){
        // For testing proposes only
        return Biome.PLAINS;
    }

    public class ChunkSection {
        private final int y;

        public ChunkSection(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public boolean isEmpty() {
            return false;
        }

        public BlockState getState(int x, int y, int z) {
            // For testing proposes
            return new BlockState();
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
