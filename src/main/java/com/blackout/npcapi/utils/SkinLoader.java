package com.blackout.npcapi.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.blackout.npcapi.core.Skin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SkinLoader {

	public static List<Skin> skins = new ArrayList<Skin>();
	
	
	/**
	 * Get a skin using his ID
	 * @param id
	 * @return
	 */
	public static Skin getSkinById(int id) {
		for (Skin s : skins)
			if (id == s.getId())
				return (s);
		
		System.err.println("Could not get skin with id " + id + " !");
		return (null);
	}
	
	/**
	 * Load a skin from Mojang server using a player UUID
	 * @param id
	 * @param uuid
	 */
	public static void loadSkinFromUUID(int id, String uuid) {
		try {
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader = new InputStreamReader(url.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();
	 
			skins.add(new Skin(id, texture, signature));
		} catch (Exception e) {
			System.err.println("Could not get skin data!");
		}
	}
	
	/**
	 * Load a skin using a player name
	 * @param id
	 * @param name
	 */
	public static void loadSkinFromName(int id, String name) {
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			String line = null;
			
			while ( (line = br.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			is.close();
			br.close();
			
			JsonObject json = new JsonParser().parse(builder.toString()).getAsJsonObject();
			
			loadSkinFromUUID(id, json.get("id").getAsString());
		} catch (Exception e) {
			System.err.println("Could not get player uuid!");
		}
	}
}
