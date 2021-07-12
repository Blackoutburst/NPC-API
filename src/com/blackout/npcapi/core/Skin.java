package com.blackout.npcapi.core;

public class Skin {

	protected int id;
	protected String value;
	protected String signature;
	
	public Skin(int id, String value, String signature) {
		this.id = id;
		this.value = value;
		this.signature = signature;
	}

	public String getValue() {
		return value;
	}

	public String getSignature() {
		return signature;
	}

	public int getId() {
		return id;
	}
	
}