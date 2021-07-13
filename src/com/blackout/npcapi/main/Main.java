package com.blackout.npcapi.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.blackout.npcapi.core.APlayer;
import com.blackout.npcapi.core.NPC;
import com.blackout.npcapi.core.PacketInteractListener;
import com.blackout.npcapi.utils.NPCManager;

public class Main extends JavaPlugin implements Listener {

	public static List<APlayer> players = new ArrayList<APlayer>();
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onMoveEvent(PlayerMoveEvent event) {
		APlayer ap = APlayer.get(event.getPlayer());
		for (NPC npc : ap.npcs) {
			double distance = Math.sqrt(
					Math.pow(event.getPlayer().getLocation().getX() - npc.getLocation().getX(), 2) +
					Math.pow(event.getPlayer().getLocation().getY() - npc.getLocation().getY(), 2) +
					Math.pow(event.getPlayer().getLocation().getZ() - npc.getLocation().getZ(), 2));
			
			if (distance > 70 && ap.npcsVisible.get(npc.getUUID())) {
				NPCManager.hideNPC(event.getPlayer(), npc);
				ap.npcsVisible.put(npc.getUUID(), false);
			}
			if (distance < 70 && !ap.npcsVisible.get(npc.getUUID())) {
				NPCManager.reloadNPC(event.getPlayer(), npc);
				ap.npcsVisible.put(npc.getUUID(), true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		players.add(new APlayer(event.getPlayer()));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		players.remove(APlayer.get(event.getPlayer()));
	}
}
