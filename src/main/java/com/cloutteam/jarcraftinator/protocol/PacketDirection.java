package com.cloutteam.jarcraftinator.protocol;

import lombok.Getter;
import lombok.Setter;

public class PacketDirection {
	
	@Getter
	@Setter
	public int in, out, sizeOffset;
	
	public PacketDirection(int in, int out, int sizeOffset) {
		this.in = in;
		this.out = out;
		this.sizeOffset = sizeOffset;
	}
	
	public PacketDirection(int in, int sizeOffset) {
		this.in = in;
		this.sizeOffset = sizeOffset;
	}
	
	public PacketDirection(boolean isOut, int out, int sizeOffset) {
		this.out = out;
		this.sizeOffset = sizeOffset;
	}

}
