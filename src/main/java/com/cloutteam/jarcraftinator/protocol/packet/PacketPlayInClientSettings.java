package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.utils.VarData;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketPlayInClientSettings extends PacketIn {

    private String locale;
    private int viewDistance;
    private int chatMode;
    private boolean chatColors;
    private int displayedSkinParts;
    private int mainHand;

    public String getLocale() {
        return locale;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getChatMode() {
        return chatMode;
    }

    public boolean isChatColors() {
        return chatColors;
    }

    public int getDisplayedSkinParts() {
        return displayedSkinParts;
    }

    public int getMainHand() {
        return mainHand;
    }

    @Override
    public void onReceive(int length, DataInputStream in) throws IOException {
        locale = VarData.readVarString(in, VarData.readVarInt(in));
        viewDistance = in.readByte();
        chatMode = VarData.readVarInt(in);
        chatColors = in.readBoolean();
        displayedSkinParts = in.readUnsignedByte();
        mainHand = VarData.readVarInt(in);
    }
}
