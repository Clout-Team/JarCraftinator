package com.cloutteam.jarcraftinator.world;

public enum DimensionType {
    OVERWORLD(0), NETHER(-1), THE_END(1);

    private final int id;

    DimensionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
