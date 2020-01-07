package com.cloutteam.jarcraftinator.protocol;

import com.cloutteam.jarcraftinator.JARCraftinator;

import lombok.Getter;

public class PlayStatePacket {

	@Getter
	public PacketDirection TELEPORT_CONFIRM, CLIENT_SETTINGS, KEEP_ALIVE, CHAT, PLAYER_POSITION_AND_LOOK, CHUNK_DATA,
			JOIN_GAME, SPAWN_POSITION;

	public PlayStatePacket() {
		
		// Param Setup for PacketDirecion (so far..)
		// (True/False if isOut only, Hex Value of Packet ID)
		TELEPORT_CONFIRM = new PacketDirection(0x00);
		CLIENT_SETTINGS = new PacketDirection(0x04);
		KEEP_ALIVE = new PacketDirection(0x0B, 0x1F);
		CHAT = new PacketDirection(0x02, 0x0F);
		PLAYER_POSITION_AND_LOOK = new PacketDirection(0x0E, 0x2F);
		CHUNK_DATA = new PacketDirection(true, 0x20);
		// 1.15.1 = 25
		// Lower = 23?
		JOIN_GAME = new PacketDirection((JARCraftinator.protocolVersion == MinecraftVersion.v1_15_1 ? 0x25 : 0x23));
		SPAWN_POSITION = new PacketDirection(true, (JARCraftinator.protocolVersion == MinecraftVersion.v1_15_1 ? 0x4D : 0x46));
	}

}
