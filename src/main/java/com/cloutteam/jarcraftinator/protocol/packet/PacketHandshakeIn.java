package com.cloutteam.jarcraftinator.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;

public class PacketHandshakeIn extends PacketIn {

    private int clientVersion;
    private String address;
    private int port;
    private NextState nextState;

    @Override
    public void onReceive(DataInputStream in) throws Exception {
        clientVersion = VarData.readVarInt(in);
        address = VarData.readVarString(in, VarData.readVarInt(in));
        port = VarData.readVarInt(in);
        nextState = NextState.getByCode(VarData.readVarInt(in));
    }

    public enum NextState {
        STATUS(1), LOGIN(2);

        private final int code;

        NextState(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static NextState getByCode(int code) {
            for (NextState state : values())
                if (state.getCode() == code) return state;
            return null;
        }
    }
}
