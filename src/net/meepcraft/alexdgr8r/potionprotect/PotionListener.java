package net.meepcraft.alexdgr8r.potionprotect;

import java.util.Collection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;

public class PotionListener implements Listener {
	
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
		
	}

}
