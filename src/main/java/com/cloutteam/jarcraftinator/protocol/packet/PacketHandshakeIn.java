package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;

public class PacketHandshakeIn extends PacketIn {

    private int clientVersion;
    private String address;
    private int port;
    private NextState nextState;

    @Override
    public void onReceive(int length, DataInputStream in) throws Exception {
        clientVersion = VarData.readVarInt(in);
        System.out.println(clientVersion);
        address = VarData.readVarString(in, VarData.readVarInt(in));
        System.out.println(address);
        port = in.readUnsignedShort();
        System.out.println(port);
        nextState = NextState.getByCode(VarData.readVarInt(in));
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public NextState getNextState() {
        return nextState;
    }

    public enum NextState {
        NONE(0), STATUS(1), LOGIN(2);

        private final int code;

        NextState(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static NextState getByCode(int code) {
            System.out.println(code);
            for (NextState state : values())
                if (state.getCode() == code) return state;
            return NONE;
        }
    }
}
