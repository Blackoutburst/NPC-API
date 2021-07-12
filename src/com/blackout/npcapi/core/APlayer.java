package com.blackout.npcapi.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.blackout.npcapi.main.Main;

public class APlayer {
	
	protected Player player;
	public Map<UUID, Boolean> npcsVisible;
	
	public APlayer (Player player) {
		this.player = player;
		this.npcsVisible = new HashMap<UUID, Boolean>();
	}
	
	public static APlayer get(Player player) {
		for (APlayer p : Main.players)
			if (p.player == player)
				return (p);
		return (null);
	}
}
