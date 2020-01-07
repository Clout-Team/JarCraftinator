package com.cloutteam.jarcraftinator.world.navigation;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.entity.Entity;
import com.cloutteam.jarcraftinator.entity.player.Player;

public class Teleport {

	private static int lastId = 0;

	private final int id;
	private final Entity entity;
	private final Location from;
	private final Location to;
	private final TeleportCause cause;

	private boolean confirmed = false;

	public Teleport(Entity entity, Location from, Location to, TeleportCause cause) {
		this.id = lastId++;
		this.entity = entity;
		this.from = from;
		this.to = to;
		this.cause = cause;
		confirmed = !(entity instanceof Player);
		JARCraftinator.getINSTANCE().getTeleportManager().addTeleport(this);
	}

	public int getId() {
		return id;
	}

	public Entity getEntity() {
		return entity;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

	public TeleportCause getCause() {
		return cause;
	}

	public void confirm() {
		confirmed = true;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public enum TeleportCause {
		CHORUS_FRUIT, COMMAND, END_GATEWAY, END_PORTAL, ENDER_PEARL, NETHER_PORTAL, PLUGIN, SPECTATE, LOGIN, UNKNOWN
	}
}
