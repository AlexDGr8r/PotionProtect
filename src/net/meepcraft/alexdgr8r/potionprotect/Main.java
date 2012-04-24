package net.meepcraft.alexdgr8r.potionprotect;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Main extends JavaPlugin {
	
	private static Logger log;
	
	public static WorldEditPlugin worldEdit;
	public static WorldGuardPlugin worldGuard;
	public static Potion potion = Potion.fromDamage(16430);
	
	public void onEnable() {
		log = this.getLogger();
		log_info("Enabling...");
		
		//Hook to WorldEdit
		worldEdit = this.getWorldEdit();
		if (worldEdit == null) {
			log_info("WorldEdit not found!");
			this.getPluginLoader().disablePlugin(this);
		} else {
			log_info(worldEdit.getName() + " Successfully hooked!");
		}
		//Hook to WorldGuard
		worldGuard = this.getWorldGuard();
		if (worldGuard == null) {
			log_info("WorldGuard not found!");
			this.getPluginLoader().disablePlugin(this);
		} else {
			log_info(worldGuard.getName() + " Successfully hooked!");
		}
		
		
		
		log_info("Enabled!");
	}
	
	public void onDisable() {
		log_info("Disabled!");
	}
	
	public static void log_info(String s) {
		log.info("[PotionProtect]" + s);
	}
	
	public WorldEditPlugin getWorldEdit() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			return null;
		}
		return (WorldEditPlugin) plugin;
	}
	
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}

}
