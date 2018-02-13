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
        return new ChunkSection(y);
    }

    public Biome getBiome(int x, int z){
        // For testing proposes only
        return Biome.PLAINS;
    }

    public class ChunkSection {
        private final int y;
	private BlockState[][][] data = new BlockState[16][16][16];

        public ChunkSection(int y) {
            this.y = y;
            generateDummyData();
        }

        public int getY() {
            return y;
        }

        public boolean isEmpty() {
            return false;
        }

	public void generateDummyData() {
            Terrain t = new Terrain(10);
            t.generate();
            for (int x=0; x<16; x++) {
                    for (int y=0; y<16; y++) {
                        for (int z=0; z<16; z++) {
                            data[x][y][z] = t.getBlock(x, y, z);
                        }
                    }
            }
        }

        public BlockState getState(int x, int y, int z) {
	    return data[x][y][z];
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
