package net.meepcraft.alexdgr8r.potionprotect;

public class ProtectPotion {
	
	public int damageID;
	public int width;
	public int length;
	public String permission;
	public String name;
	
	private int height; //Here so we can determine the height of the plot based on current Y value
	
	public ProtectPotion(String Name, int l, int w, int h, int id, String perm) {
		name = Name;
		length = l;
		width = w;
		height = h;
		damageID = id;
		permission = perm;
	}
	
	public int getMaxHeight(int y) {
		if (height >= 256 || height <= 0) {
			return 256;
		}
		int mHeight = (height / 2) + y;
		return mHeight > 256 ? 256 : mHeight;
	}
	
	public int getMinHeight(int y) {
		if (height >= 256 || height <= 0) {
			return 0;
		}
		int mHeight = y - (height / 2);
		return mHeight < 0 ? 0 : mHeight;
	}

}
