package com.blackout.npcapi.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

	public static List<NPC> npcs = new ArrayList<NPC>();
	
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
		
		npc.setEntityId(npcEntity.getId())
		.setEntity(npcEntity);
		npcs.add(npc);
		
		sendPacket(p, watcher, npc);
		
		APlayer ap = APlayer.get(p);
		ap.npcsVisible.put(npc.getUUID(), true);
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
		npcs.remove(npc);
		
		APlayer ap = APlayer.get(p);
		ap.npcsVisible.remove(npc.getUUID());
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
}
