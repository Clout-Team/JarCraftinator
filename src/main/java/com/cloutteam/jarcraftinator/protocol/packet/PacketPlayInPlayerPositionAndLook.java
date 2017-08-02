package com.cloutteam.jarcraftinator.protocol.packet;

import java.io.DataInputStream;

public class PacketPlayInPlayerPositionAndLook extends PacketIn{

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void onReceive(int length, DataInputStream in) throws Exception {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        yaw = in.readFloat();
        pitch = in.readFloat();
        onGround = in.readBoolean();
    }
}
