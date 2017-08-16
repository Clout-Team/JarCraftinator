package com.cloutteam.jarcraftinator.world;

public enum Biome {

    OCEAN(0), PLAINS(1), VOID(127);

    private final int id;

    Biome(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
