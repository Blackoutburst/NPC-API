package com.blackout.npcapi.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.blackout.npcapi.core.NPC;
import com.blackout.npcapi.main.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
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
	
	public static void spawnNPC(NPC npc, Player p) {
		WorldServer s = ((CraftWorld) npc.getLocation().getWorld()).getHandle();
		World w = ((CraftWorld) npc.getLocation().getWorld()).getHandle();
		
		GameProfile gp = new GameProfile(npc.getUUID(), npc.getName());
		gp.getProperties().put("textures", new Property("textures", npc.getSkin().getValue(), npc.getSkin().getSignature()));
	
		EntityPlayer npcEntity = new EntityPlayer(MinecraftServer.getServer(), s, gp, new PlayerInteractManager(w));
	
		DataWatcher watcher = npcEntity.getDataWatcher();
		watcher.watch(10, (byte) (npc.isCapeVisible() ? 127 : 126));
		
		npcEntity.setLocation(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), npc.getLocation().getYaw(), npc.getLocation().getPitch());
		
		PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(npcEntity, (byte) ((npc.getLocation().getYaw() * 256.0F) / 360.0F));
		PacketPlayOutPlayerInfo addPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npcEntity);
		PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(npcEntity);
		PacketPlayOutEntityMetadata dataPacket = new PacketPlayOutEntityMetadata(npcEntity.getId(), watcher, true);
		PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity);
	
		PacketPlayOutAnimation armSwing = new PacketPlayOutAnimation(npcEntity, 0);
		
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		connection.sendPacket(addPacket);
		connection.sendPacket(spawnPacket);
		connection.sendPacket(dataPacket);
		connection.sendPacket(headRotationPacket);
		connection.sendPacket(armSwing);
		
		npcs.add(npc);
		
		new BukkitRunnable() {
			public void run() {
				connection.sendPacket(removePacket);
			}
		}.runTaskLater(Main.getPlugin(Main.class), 100L);
	}
}
