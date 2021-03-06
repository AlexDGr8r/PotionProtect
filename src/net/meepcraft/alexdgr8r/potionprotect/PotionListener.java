package net.meepcraft.alexdgr8r.potionprotect;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class PotionListener implements Listener {
	
	private Main plugin;
	
	public PotionListener(Main instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void potionSplash(PotionSplashEvent event) {
		Collection<PotionEffect> effects = event.getPotion().getEffects();
		ProtectPotion proPot = null;
		for (ProtectPotion potion : Main.protectPotions) {
			if (effects.size() == potion.potionEffects.size() && effects.containsAll(potion.potionEffects)) {
				proPot = potion;
				break;
			}
		}
		if (proPot == null) return;
		
		ThrownPotion potionEntity = event.getPotion();
		Player player = (Player)potionEntity.getShooter(); // We'll just cast this for now
		if (!player.hasPermission(proPot.permission)) {
			player.sendMessage(ChatColor.RED + "You do not have permission to protect land with this potion.");
			player.getInventory().addItem(new ItemStack(373, 1, (short)proPot.damageID));
			event.setCancelled(true);
			return;
		}
		if (plugin.getOwnedPlots(player) >= getMaxPlots(player) && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You own too many plots. ");
			player.getInventory().addItem(new ItemStack(373, 1, (short)proPot.damageID));
			event.setCancelled(true);
			return;
		}
		
		// Create Region
		String regionName = player.getName().toLowerCase() + getUniqueID(player, getRegionManager(potionEntity));
		BlockVector pos1 = getPos1(potionEntity, proPot);
		BlockVector pos2 = getPos2(potionEntity, proPot);
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, pos1, pos2);
		region.setOwners(getDefaultDomain(player));
		if (getRegionManager(potionEntity).overlapsUnownedRegion(region, getLocalPlayer(player))) {
			player.sendMessage(ChatColor.RED + "There is already an area being protected here or nearby!");
			player.getInventory().addItem(new ItemStack(373, 1, (short)proPot.damageID));
			event.setCancelled(true);
		} else {
			getRegionManager(potionEntity).addRegion(region);
			try {
				getRegionManager(potionEntity).save();
			} catch (ProtectionDatabaseException e) {
				e.printStackTrace();
			}
			
			// Add fence border
			int y = potionEntity.getLocation().getBlockY();
			int lowX = pos1.getBlockX();
			int highX = pos2.getBlockX();
			int lowZ = pos1.getBlockZ();
			int highZ = pos2.getBlockZ();
			World world = potionEntity.getLocation().getWorld();
			for (int x = lowX; x <= highX; x++) {
				Block b1 = world.getBlockAt(x, y, lowZ);
				Block b2 = world.getBlockAt(x, y, highZ);
				if (isReplaceableBlock(b1.getTypeId())) b1.setTypeId(85);
				if (isReplaceableBlock(b2.getTypeId())) b2.setTypeId(85);
			}
			for (int z = lowZ; z <= highZ; z++) {
				Block b1 = world.getBlockAt(lowX, y, z);
				Block b2 = world.getBlockAt(highX, y, z);
				if (isReplaceableBlock(b1.getTypeId())) b1.setTypeId(85);
				if (isReplaceableBlock(b2.getTypeId())) b2.setTypeId(85);
			}
			
			player.sendMessage(ChatColor.GREEN + "Your land is now protected!");
		}
	}
	
	private boolean isReplaceableBlock(int id) {
		switch (id) {
		case 0:
		case 31:
		case 32:
		case 51:
		case 78:
		case 106:
			return true;
		}
		return false;
	}
	
	private RegionManager getRegionManager(ThrownPotion entity) {
		return Main.worldGuard.getRegionManager(entity.getLocation().getWorld());
	}
	
	private DefaultDomain getDefaultDomain(Player player) {
		DefaultDomain domain = new DefaultDomain();
		domain.addPlayer(getLocalPlayer(player));
		return domain;
	}
	
	private LocalPlayer getLocalPlayer(Player player) {
		return Main.worldGuard.wrapPlayer(player);
	}
	
	private int getUniqueID(Player player, RegionManager manager) {
		String playerName = player.getName().toLowerCase();
		int biggest = 1;
		for (String regionName : manager.getRegions().keySet()) {
			if (!regionName.startsWith(playerName)) continue;
			try {
				int id = Integer.valueOf(regionName.replace(playerName, "")).intValue();
				if (id >= biggest) biggest = id + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return biggest;
	}
	
	// This is bottom slice of region
	private BlockVector getPos1(ThrownPotion entity, ProtectPotion proPot) {
		int radiusX; // Width
		int radiusZ; // Length
		if (proPot.width % 2 == 0) {
			radiusX = proPot.width / 2;
		} else {
			radiusX = (int)(proPot.width / 2 - 0.5D);
		}
		if (proPot.length % 2 == 0) {
			radiusZ = proPot.length / 2;
		} else {
			radiusZ = (int)(proPot.width / 2 - 0.5D);
		}
		return new BlockVector(entity.getLocation().getBlockX() - radiusX, 
				proPot.getMinHeight(entity.getLocation().getBlockY()),
				entity.getLocation().getBlockZ() - radiusZ);
	}
	
	// This is top slice of region
	private BlockVector getPos2(ThrownPotion entity, ProtectPotion proPot) {
		int radiusX; // Width
		int radiusZ; // Length
		if (proPot.width % 2 == 0) {
			radiusX = proPot.width / 2 - 1;
		} else {
			radiusX = (int)(proPot.width / 2 - 0.5D);
		}
		if (proPot.length % 2 == 0) {
			radiusZ = proPot.length / 2 - 1;
		} else {
			radiusZ = (int)(proPot.width / 2 - 0.5D);
		}
		return new BlockVector(entity.getLocation().getBlockX() + radiusX,
				proPot.getMaxHeight(entity.getLocation().getBlockY()),
				entity.getLocation().getBlockZ() + radiusZ);
	}
	
	private int getMaxPlots(Player player) {
		int maxPlots = 0;
		for (String s : Main.plotNumberPerms.keySet()) {
			if (player.hasPermission(s)) {
				if (Main.plotNumberPerms.get(s) >= maxPlots) {
					maxPlots = Main.plotNumberPerms.get(s);
				}
			}
		}
		return maxPlots;
	}

}
