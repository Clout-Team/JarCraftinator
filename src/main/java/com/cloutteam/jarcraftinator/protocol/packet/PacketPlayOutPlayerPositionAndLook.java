package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;
import com.cloutteam.jarcraftinator.world.navigation.Location;

import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPlayOutPlayerPositionAndLook extends PacketOut {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private byte flags;
    private int teleportID;

    public PacketPlayOutPlayerPositionAndLook(double x, double y, double z, float yaw, float pitch, byte flags, int teleportID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
        this.teleportID = teleportID;
    }

    public PacketPlayOutPlayerPositionAndLook(Location location, byte flags, int teleportID) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.flags = flags;
        this.teleportID = teleportID;
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

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public int getTeleportID() {
        return teleportID;
    }

    public void setTeleportID(int teleportID) {
        this.teleportID = teleportID;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
        byte[] packetId = VarData.getVarInt(MinecraftPacket.PLAY.PLAYER_POSITION_AND_LOOK.out);
        byte[] teleportID = VarData.getVarInt(this.teleportID);
        VarData.writeVarInt(out, packetId.length + teleportID.length + 33);
        out.write(packetId);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);
        out.writeByte(flags);
        out.write(teleportID);
        out.flush();
        System.out.println("PacketPlayOutPlayerPositionAndLook");
    }
}
