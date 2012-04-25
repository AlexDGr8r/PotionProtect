package net.meepcraft.alexdgr8r.potionprotect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class Main extends JavaPlugin {
	
	private static Logger log;
	
	public static WorldEditPlugin worldEdit;
	public static WorldGuardPlugin worldGuard;
	public static List<ProtectPotion> protectPotions = new ArrayList<ProtectPotion>();
	public static HashMap<String, Integer> plotNumberPerms = new HashMap<String, Integer>();
	
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
		
		this.getServer().getPluginManager().registerEvents(new PotionListener(this), this);
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
		ConfigurationSection section = config.getConfigurationSection("PlotsByPermission");
		Map<String, Object> secValues = section.getValues(false);
		for (String s : secValues.keySet()) {
			plotNumberPerms.put("potion." + s, (Integer)secValues.get(s));
		}
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
		List<Integer> potionIDs = new ArrayList<Integer>();
		for (ProtectPotion potion : protectPotions) {
			config.set("Potions." + potion.damageID + ".Height", potion.getHeight());
			config.set("Potions." + potion.damageID + ".Width", potion.width);
			config.set("Potions." + potion.damageID + ".Length", potion.length);
			config.set("Potions." + potion.damageID + ".Permission", potion.permission);
			potionIDs.add(potion.damageID);
		}
		config.set("PotionIDs", potionIDs);
		for (String s : plotNumberPerms.keySet()) {
			config.set("PlotsByPermission." + s.replace("potion.", ""), plotNumberPerms.get(s));
		}
		this.saveConfig();
	}
	
	public int getOwnedPlots(Player player) {
		int plots = 0;
		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
		for (World world : this.getServer().getWorlds()) {
			RegionManager manager = worldGuard.getRegionManager(world);
			plots += manager.getRegionCountOfPlayer(localPlayer);
		}
		return plots;
	}

}
