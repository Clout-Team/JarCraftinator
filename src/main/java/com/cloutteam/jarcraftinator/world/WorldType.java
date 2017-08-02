package com.cloutteam.jarcraftinator.world;

public enum WorldType {
    FLAT(0), DEFAULT(1);
    private final int id;
    WorldType(int id) { this.id = id;}
    public int getId() { return this.id;}
    public static WorldType fromInt(int id) {
        for (WorldType i: WorldType.values()) {
            if (i.getId() == id) { return i;}
        }
        return null;
    }
}
