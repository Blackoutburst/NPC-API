package com.blackout.npcapi.utils;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.blackout.npcapi.core.APlayer;
import com.blackout.npcapi.core.NPC;
import com.blackout.npcapi.main.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NPCManager {

	/**
	 * Destroy the NPC when the player get too far from it
	 * @param p
	 * @param npc
	 */
	public static void hideNPC(Player p, NPC npc) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc.getEntity()));
		connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getEntityId()));
	}
	
	/**
	 * Respawn the NPC when the player get close enough to it
	 * @param p
	 * @param npc
	 */
	public static void reloadNPC(Player p, NPC npc) {
		DataWatcher watcher = npc.getEntity().getDataWatcher();
		watcher.watch(10, (byte) (npc.isCapeVisible() ? 127 : 126));
		
		sendPacket(p, watcher, npc);
	}
	
	/**
	 * Spawn the NPC when at a specific location
	 * @param npc
	 * @param p
	 */
	public static void spawnNPC(NPC npc, Player p) {
		WorldServer s = ((CraftWorld) npc.getLocation().getWorld()).getHandle();
		World w = ((CraftWorld) npc.getLocation().getWorld()).getHandle();
		
		GameProfile gp = new GameProfile(npc.getUUID(), npc.getName());
		gp.getProperties().put("textures", new Property("textures", npc.getSkin().getValue(), npc.getSkin().getSignature()));
	
		EntityPlayer npcEntity = new EntityPlayer(MinecraftServer.getServer(), s, gp, new PlayerInteractManager(w));
	
		DataWatcher watcher = npcEntity.getDataWatcher();
		watcher.watch(10, (byte) (npc.isCapeVisible() ? 127 : 126));
		
		npcEntity.setLocation(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), npc.getLocation().getYaw(), npc.getLocation().getPitch());
		
		APlayer ap = APlayer.get(p);
		
		npc.setEntityId(npcEntity.getId())
		.setEntity(npcEntity);
		ap.npcs.add(npc);
		
		sendPacket(p, watcher, npc);
		
		ap.npcsVisible.put(npc.getUUID(), true);
		hideName(p, npc);
	}
	
	/**
	 * Remove a NPC
	 * @param p
	 * @param npc
	 */
	public static void deleteNPC(Player p, NPC npc) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc.getEntity()));
		connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getEntityId()));
		
		APlayer ap = APlayer.get(p);
		
		ap.npcs.remove(npc);
		ap.npcsVisible.remove(npc.getUUID());
		showName(p, npc);
	}
	
	/**
	 * Send packet used to spawn a NPC
	 * @param p
	 * @param watcher
	 * @param npc
	 */
	private static void sendPacket(Player p, DataWatcher watcher, NPC npc) {
		PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(npc.getEntity(), (byte) ((npc.getLocation().getYaw() * 256.0F) / 360.0F));
		PacketPlayOutPlayerInfo addPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc.getEntity());
		PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(npc.getEntity());
		PacketPlayOutEntityMetadata dataPacket = new PacketPlayOutEntityMetadata(npc.getEntityId(), watcher, true);
		PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc.getEntity());
		PacketPlayOutAnimation armSwing = new PacketPlayOutAnimation(npc.getEntity(), 0);
		
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		connection.sendPacket(addPacket);
		connection.sendPacket(spawnPacket);
		connection.sendPacket(dataPacket);
		connection.sendPacket(headRotationPacket);
		connection.sendPacket(armSwing);
		
		new BukkitRunnable() {
			public void run() {
				connection.sendPacket(removePacket);
			}
		}.runTaskLater(Main.getPlugin(Main.class), 100L);
	}
	
	/**
	 * Put the NPC in a special team to hide their name
	 * @param p
	 * @param npc
	 */
	public static void hideName(Player p, NPC npc) {
		if (npc.isNameVisible()) return;
		Scoreboard scoreboard = p.getScoreboard();
		Team team = null;
		
		if (scoreboard.getTeam("NPC") == null) {
			scoreboard.registerNewTeam("NPC");
		}
		team = scoreboard.getTeam("NPC");

	    team.addEntry(npc.getName());
        team.setNameTagVisibility(NameTagVisibility.NEVER);
	}
	
	/**
	 * Remove a NPC from the NPC team to display back his name
	 * @param p
	 * @param npc
	 */
	public static void showName(Player p, NPC npc) {
		if (npc.isNameVisible()) return;
		Scoreboard scoreboard = p.getScoreboard();
		Team team = null;
		
		if (scoreboard.getTeam("NPC") == null) {
			scoreboard.registerNewTeam("NPC");
		}
		team = scoreboard.getTeam("NPC");
	    team.removeEntry(npc.getName());
	}
}
