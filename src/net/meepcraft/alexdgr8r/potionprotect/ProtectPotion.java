package net.meepcraft.alexdgr8r.potionprotect;

public class ProtectPotion {
	
	public int damageID;
	public int width;
	public int length;
	public String permission;
	public String name;
	
	public ProtectPotion(String Name, int l, int w, int id, String perm) {
		name = Name;
		length = l;
		width = w;
		damageID = id;
		permission = perm;
	}

}
