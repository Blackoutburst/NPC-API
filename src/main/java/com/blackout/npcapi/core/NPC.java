package com.blackout.npcapi.core;

import java.util.UUID;

import org.bukkit.Location;

import net.minecraft.server.v1_8_R3.EntityPlayer;

public class NPC {
	
	protected UUID uuid;
	protected String name;
	protected Location location;
	protected Skin skin;
	protected boolean capeVisible;
	protected int entityId;
	protected EntityPlayer entity;
	protected boolean nameVisible;
	
	public NPC (UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.location = null;
		this.skin = null;
		this.capeVisible = true;
		this.entityId = -1;
		this.entity = null;
		this.nameVisible = true;
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

	public int getEntityId() {
		return entityId;
	}

	public NPC setEntityId(int entityId) {
		this.entityId = entityId;
		return (this);
	}

	public EntityPlayer getEntity() {
		return entity;
	}

	public NPC setEntity(EntityPlayer entity) {
		this.entity = entity;
		return (this);
	}
	
	public boolean isNameVisible() {
		return nameVisible;
	}

	public NPC setNameVisible(boolean nameVisible) {
		this.nameVisible = nameVisible;
		return (this);
	}
}
