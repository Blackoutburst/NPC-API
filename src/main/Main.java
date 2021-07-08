package main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onMoveEvent(PlayerMoveEvent event) {
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
	}
}
