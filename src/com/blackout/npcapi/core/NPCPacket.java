package com.blackout.npcapi.core;

import org.bukkit.entity.Player;

public interface NPCPacket {
	public void onLeftClick(Player player, int id);
	public void onRightClick(Player player, int id);
}
