[![License](https://img.shields.io/github/license/Blackoutburst/NPC-API.svg)](LICENSE)
# NPC-API
Simple API to create Minecraft NPC using spigot/bukkit
Paper/Spigot version 1.8.8 (v1_8_R3)

## Usage

Pre load skin (most likely on plugin startup)
```java
SkinLoader.loadSkinFromUUID(skinId, "uuid of the player skin");
```
note the skin id must be unique

Spawn NPC
```java
NPC npc = new NPC(UUID.randomUUID(), "Your NPC name")
		.setLocation(loc) // Set the npc location / looking direction
		.setSkin(SkinLoader.getSkinById(0)) // Get the skin you are supposed to pre load using his id
		.setCapeVisible(true); // Display or not the NPC cape and more option available like displaying the name
		
		NPCManager.spawnNPC(npc, event.getPlayer()); // Spawn your new NPC
```

This is still WIP and this will never be a big project just a tool to create NPC faster for my future plugins
