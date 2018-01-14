package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInChat extends PacketIn {

    private boolean isValid;
    private String message;

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException {
        int stringSize = VarData.readVarInt(in);

        if(stringSize > 256){
            isValid = false;
            message = null;
        }else {
            isValid = true;
            message = VarData.readVarString(in, stringSize);
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
