package com.cloutteam.jarcraftinator.world;

public class World {

    private final String name;
    private final DimensionType type;

    public World(String name, DimensionType type) {
        this.name = name;
        this.type = type;
    }

    public boolean hasSkylight() {
        return type == DimensionType.OVERWORLD;
    }

    public String getName() {
        return name;
    }

    public DimensionType getType() {
        return type;
    }
}
