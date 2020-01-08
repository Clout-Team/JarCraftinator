package com.cloutteam.jarcraftinator.protocol.packet;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.protocol.MinecraftVersion;
import com.cloutteam.jarcraftinator.protocol.PlayStatePacket;

import lombok.Getter;

public class MinecraftPacket {
	
	@Getter
	private PlayStatePacket PLAY;
	
	public MinecraftPacket() {
		PLAY = new PlayStatePacket();
	}

    public class HANDSHAKE {

        public static final int modern = 0x00;
        public static final int legacy = 0xFE;

    }

    public class STATUS {

        public static final int RESPONSE = 0x00;
        public static final int PONG = 0x01;

    }

    public class LOGIN {

        public class LOGIN_START {
            public static final int in = 0x00;
        }

        public class LOGIN_SUCCESS {
            public static final int out = 0x02;
        }

    }

    @Getter
    public class PLAY {

        public class TELEPORT_CONFIRM {
            public static final int in = 0x00;
        }

        public class CLIENT_SETTINGS {
            public static final int in = 0x05;
        }

        public class KEEP_ALIVE {
            public static final int in = 0x0B;
            public static final int out = 0x1F;
        }

        public class CHAT {
            public static final int in = 0x02;
            public static final int out = 0x0F;
        }

        public class PLAYER_POSITION_AND_LOOK {
            public static final int in = 0x12;
            public static final int out = 0x12;
        }

        public class CHUNK_DATA {
            public static final int out = 0x20;
        }

        public class JOIN_GAME {
            public static final short out = 0x25;
        }

        // 1.15.1 =  0x45
        // Lower is 0x46?
        public class SPAWN_POSITION {
            public final int out = (JARCraftinator.protocolVersion == MinecraftVersion.v1_15_1 ? 0x45 : 0x46);
        }

    }

}
