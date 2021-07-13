[![License](https://img.shields.io/github/license/Blackoutburst/NPC-API.svg)](LICENSE)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/82aed48c64d048ba80a044e15d6d97ec)](https://www.codacy.com/gh/Blackoutburst/NPC-API/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Blackoutburst/NPC-API&amp;utm_campaign=Badge_Grade)
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

Create a new class used to listen the interaction with the NCP
```java
public class YourClassName implements NPCPacket {

	@Override
	public void onLeftClick(Player player, int id) {
		APlayer ap = APlayer.get(player);
		for (NPC npc : ap.npcs) {
			if (id == npc.getEntityId()) {
				System.out.println(npc.getName());
			}
		}
	}

	@Override
	public void onRightClick(Player player, int id) {
		APlayer ap = APlayer.get(player);
		for (NPC npc : ap.npcs) {
			if (id == npc.getEntityId()) {
				System.out.println(npc.getName());
			}
		}
	}
}
```

On player join create the packet listener
```java
PacketInteractListener.init(event.getPlayer(), new YourClassName()); //NOTE: YourClassName must be the name of you class implementing NPCPacket
```
And remove it when the player leave
```java
PacketInteractListener.remove(event.getPlayer());
```

This is still WIP and this will never be a big project just a tool to create NPC faster for my future plugins
