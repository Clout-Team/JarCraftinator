package com.cloutteam.jarcraftinator.world;

public class BlockState {
    private final int id;
    private final int meta;

    public BlockState(int blockId, int blockMeta) {
	this.id = blockId;
	this.meta = blockMeta;
    }

    public int getId() {
	return id;
    }

    public int getMetadata() {
	return meta;
    }
}
