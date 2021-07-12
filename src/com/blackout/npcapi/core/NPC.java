package com.blackout.npcapi.core;

import java.util.UUID;

import org.bukkit.Location;

public class NPC {
	
	protected UUID uuid;
	protected String name;
	protected Location location;
	protected Skin skin;
	protected boolean capeVisible;
	
	public NPC (UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.location = null;
		this.skin = null;
		this.capeVisible = true;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public NPC setLocation(Location location) {
		this.location = location;
		return (this);
	}

	public Skin getSkin() {
		return skin;
	}

	public NPC setSkin(Skin skin) {
		this.skin = skin;
		return (this);
	}

	public boolean isCapeVisible() {
		return capeVisible;
	}

	public NPC setCapeVisible(boolean capeVisible) {
		this.capeVisible = capeVisible;
		return (this);
	}
	
}
