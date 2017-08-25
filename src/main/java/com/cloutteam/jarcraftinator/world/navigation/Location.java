package com.cloutteam.jarcraftinator.world.navigation;

import com.cloutteam.jarcraftinator.api.json.JSONObject;
import com.cloutteam.jarcraftinator.world.World;

public class Location {

    private World world;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private float yaw = 0;
    private float pitch = 0;

    public Location(World world, double x, double y, double z, float yaw, float pitch) {
        if (world == null) throw new IllegalArgumentException("World cannot be null");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(World world, double x, double y, double z) {
        if (world == null) throw new IllegalArgumentException("World cannot be null");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        if (world == null) throw new IllegalArgumentException("World cannot be null");
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public JSONObject toJSON() {
        return new JSONObject().add("world", world.getName()).add("x", x).add("y", y).add("z", z).add("yaw", yaw).add("pitch", pitch);
    }

    public String toJSONString() {
        return toJSON().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return Double.compare(location.x, x) == 0 && Double.compare(location.y, y) == 0 && Double.compare(location.z, z) == 0 && Float.compare(location.yaw, yaw) == 0 && Float.compare(location.pitch, pitch) == 0 && world.equals(location.world);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = world.hashCode();
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        return result;
    }

    public boolean equalsIgnoreDirection(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return Double.compare(location.x, x) == 0 && Double.compare(location.y, y) == 0 && Double.compare(location.z, z) == 0 && world.equals(location.world);
    }

}
