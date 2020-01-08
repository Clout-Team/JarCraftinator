package com.cloutteam.jarcraftinator.protocol.packet;

public class MinecraftPacket {

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

	    public class PLAY {

	        public class TELEPORT_CONFIRM {
	            public static final int in = 0x00;
	        }

	        public class CLIENT_SETTINGS {
	            public static final int in = 0x05;
	        }

	        public class KEEP_ALIVE {
	            public static final int in = 0x0B;
	            public static final int out = 0x21;
	        }

	        public class CHAT {
	            public static final int in = 0x03;
	            public static final int out = 0x0F;
	        }

	        public class PLAYER_POSITION_AND_LOOK {
	            public static final int in = 0x12;
	            public static final int out = 0x36;
	        }

	        public class CHUNK_DATA {
	            public static final int out = 0x22;
	        }

	        public class JOIN_GAME {
	            public static final int out = 0x26;
	        }

	        public class SPAWN_POSITION {
	            public static final int out = 0x4D;
	        }

	    }

	}