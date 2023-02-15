package com.blackout.npcapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.blackout.npcapi.main.Main;

public class APlayer {
	
	public Player player;
	public Map<UUID, Boolean> npcsVisible;
	public List<NPC> npcs;
	
	public APlayer (Player player) {
		this.player = player;
		this.npcsVisible = new HashMap<UUID, Boolean>();
		this.npcs = new ArrayList<NPC>();
	}
	
	public static APlayer get(Player player) {
		for (APlayer p : Main.players)
			if (p.player.getUniqueId().equals(player.getUniqueId()))
				return (p);
		return (null);
	}
}
