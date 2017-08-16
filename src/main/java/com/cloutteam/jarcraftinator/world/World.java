package com.cloutteam.jarcraftinator.world;

import com.cloutteam.jarcraftinator.api.DimensionType;

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

}
