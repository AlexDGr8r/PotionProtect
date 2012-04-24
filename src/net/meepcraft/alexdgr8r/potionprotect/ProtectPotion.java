package net.meepcraft.alexdgr8r.potionprotect;

import java.util.Collection;

import org.bukkit.potion.PotionEffect;

public class ProtectPotion {
	
	public int damageID;
	public int width;
	public int length;
	public String permission;
	public Collection<PotionEffect> potionEffects;
	
	private int height; //Here so we can determine the height of the plot based on current Y value
	
	public ProtectPotion(Collection<PotionEffect> pEffects, int l, int w, int h, int id, String perm) {
		potionEffects = pEffects;
		length = l;
		width = w;
		height = h;
		damageID = id;
		permission = perm;
	}
	
	// Gets maximum height of region based off of Y value
	public int getMaxHeight(int y) {
		if (height >= 256 || height <= 0) {
			return 256;
		}
		int mHeight = (height / 2) + y;
		return mHeight > 256 ? 256 : mHeight;
	}
	
	// Gets minimum height of region based off of Y value
	public int getMinHeight(int y) {
		if (height >= 256 || height <= 0) {
			return 0;
		}
		int mHeight = y - (height / 2);
		return mHeight < 0 ? 0 : mHeight;
	}
	
	// For configuration purposes ONLY
	public int getHeight() {
		return height;
	}

}
