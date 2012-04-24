package net.meepcraft.alexdgr8r.potionprotect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Main extends JavaPlugin {
	
	private static Logger log;
	
	public static WorldEditPlugin worldEdit;
	public static WorldGuardPlugin worldGuard;
	public static List<ProtectPotion> protectPotions = new ArrayList<ProtectPotion>();
	public static int maxPlotsPerPlayer;
	
	public FileConfiguration config;
	
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
		
		loadConfiguration();
		
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
	
	public void loadConfiguration() {
		config = this.getConfig();
		List<Integer> potionIDs = config.getIntegerList("PotionIDs");
		maxPlotsPerPlayer = config.getInt("MaxPlotsPerPlayer", 3);
		// Load potions with defaults
		for (int i = 0; i < potionIDs.size(); i++) {
			Potion potion = Potion.fromDamage(potionIDs.get(i));
			int h = config.getInt("Potions." + potionIDs.get(i) + ".Height", 256);
			int w = config.getInt("Potions." + potionIDs.get(i) + ".Width", 10);
			int l = config.getInt("Potions." + potionIDs.get(i) + ".Length", 10);
			String perm = config.getString("Potions." + potionIDs.get(i) + ".Permission", "potion." + potionIDs.get(i));
			protectPotions.add(new ProtectPotion(potion.getEffects(), l, w, h, potionIDs.get(i), perm));
		}
		saveConfiguration();
	}
	
	public void saveConfiguration() {
		config.set("MaxPlotsPerPlayer", maxPlotsPerPlayer);
		for (ProtectPotion potion : protectPotions) {
			config.set("Potions." + potion.damageID + ".Height", potion.getHeight());
			config.set("Potions." + potion.damageID + ".Width", potion.width);
			config.set("Potions." + potion.damageID + ".Length", potion.length);
			config.set("Potions." + potion.damageID + ".Permission", potion.permission);
		}
		this.saveConfig();
	}

}
