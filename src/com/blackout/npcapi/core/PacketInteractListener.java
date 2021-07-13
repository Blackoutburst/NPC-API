package com.blackout.npcapi.core;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

public class PacketInteractListener {

	/**
	 * Create the Entity interact packet listener
	 * @param player
	 * @param channelField
	 */
	public static void init(Player player, NPCPacket npcInteract) {
		Field actionField = null;
		try {
			actionField = PacketPlayInUseEntity.class.getDeclaredField("a");
			actionField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Field channelField = null;
		for (Field field : NetworkManager.class.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(Channel.class)) {
				channelField = field;
				break;
			}
		}
		createlisten(player, actionField, channelField, npcInteract);
	}
	
	/**
	 * Create the Entity interact packet listener
	 * @param player
	 * @param channelField
	 */
	@SuppressWarnings("rawtypes")
	private static void createlisten(Player player, Field actionField, Field channelField, NPCPacket npcInteract) {
		try {
			Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
			if (channel != null) {
				channel.pipeline().addAfter("decoder", "ENTITY_USE", new MessageToMessageDecoder<Packet>() {
					@Override
					protected void decode(ChannelHandlerContext chc, Packet packet, List<Object> out) throws Exception {
						if (packet instanceof PacketPlayInUseEntity) {
							PacketPlayInUseEntity usePacket = (PacketPlayInUseEntity) packet;
							if (usePacket.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT || 
								usePacket.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
								npcInteract.onRightClick(player, actionField.getInt(usePacket));
							}
							if (usePacket.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
								npcInteract.onLeftClick(player, actionField.getInt(usePacket));
							}
						}
						out.add(packet);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete the Entity interact packet listener
	 * @param player
	 * @param channelField
	 */
	public static void remove(Player player) {
		Field channelField = null;
		for (Field field : NetworkManager.class.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(Channel.class)) {
				channelField = field;
				break;
			}
		}
		deleteListener(player, channelField);
	}
	
	/**
	 * Delete the Entity interact packet listener
	 * @param player
	 * @param channelField
	 */
	private static void deleteListener(Player player, Field channelField) {
		try {
			Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
			if (channel != null && channel.pipeline().get("ENTITY_USE") != null) {
				channel.pipeline().remove("ENTITY_USE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
