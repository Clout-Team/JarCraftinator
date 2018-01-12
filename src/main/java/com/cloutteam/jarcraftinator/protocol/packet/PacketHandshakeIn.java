package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.exceptions.IOWriteException;
import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketHandshakeIn extends PacketIn {

    private int clientVersion;
    private String address;
    private int port;
    private NextState nextState;

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException, IOWriteException {
        clientVersion = VarData.readVarInt(in);
        address = VarData.readVarString(in, VarData.readVarInt(in));
        port = in.readUnsignedShort();
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
