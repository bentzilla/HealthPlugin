package com.fsscripts.bentzilla;

import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public final class HRegen extends TimerTask {
	
	private static Health plugin;
    static Timer hTimer = new Timer();
    static int hAmount;
    static boolean hActive;
    
	public HRegen(Health plugin) {
		this.plugin = plugin;
	}
	
	public static void StartHRegen(int Amount, int Interval) {
		hAmount = Amount;
		hTimer.schedule(new HRegen(plugin), 0, Interval * 1000 /*x Seconds*/);
		hActive = true;
	}
  
	public static void StopHRegen() {
		hTimer.cancel();
		hActive = false;
	}

	public void run(){
		int curHP;
		int maxHP = 20;
		for (Player P : plugin.getServer().getOnlinePlayers()){
			curHP = P.getHealth();
			if(curHP < maxHP && curHP > 0 && hAmount > 0){
				P.setHealth(curHP + hAmount);
				if(P.getHealth() > 20){
					  P.setHealth(20);
				}
			}
	    }
	}
}