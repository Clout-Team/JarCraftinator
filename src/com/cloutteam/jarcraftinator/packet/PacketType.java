package com.cloutteam.jarcraftinator.packet;

public enum PacketType {

    SLI_HANDSHAKE(0x00, PacketOrigin.CLIENT), SLI_RESPONSE(0x00, PacketOrigin.SERVER), SLI_PING(0x01, PacketOrigin.CLIENT), SLI_PONG(0x01, PacketOrigin.SERVER);

    private final int id;
    private final PacketOrigin origin;

    PacketType(int id, PacketOrigin origin) {
        this.id = id;
        this.origin = origin;
    }

    public int getId(){
        return id;
    }

    public PacketOrigin getOrigin(){
        return origin;
    }

}
