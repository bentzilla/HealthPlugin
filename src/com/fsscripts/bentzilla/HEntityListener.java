package com.fsscripts.bentzilla;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

//import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * Handle events for all Entity related events
 * @author Bentzilla
 */

public class HEntityListener extends EntityListener {
    private final Health plugin;
    //private Permissions HPermissions = null;

    public HEntityListener(Health instance) {
        plugin = instance;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
    	Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = ((Player) entity); 
		//DamageCause type = null;
		if (Health.damageHealthEnabled(player)) {
			CancelHealth(event.getCause(), event);
		}
    }
    
    public boolean CancelHealth(DamageCause damagetype, EntityDamageEvent event){
    	//if (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) {
    	if (event.getCause() == damagetype){
    		event.setCancelled(true);
    		return true;
    	}
    	return false;
    }
}

