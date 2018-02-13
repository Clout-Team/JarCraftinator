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
        return false;
    }

    public void setSections(ChunkSection[] chunkSections) {
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


}
