package com.fsscripts.bentzilla;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Health extends JavaPlugin {
    
	private String HVersion = "1.6";
	
    public final static HashMap<Player, String> chatHealthUsers = new HashMap<Player,String>(); 
    public final static HashMap<Player, Boolean> damageHealthUsers = new HashMap<Player,Boolean>(); 
    
    private final HPlayerChatListener ThePlayerChatListener = new HPlayerChatListener(this);
    private final HPlayerListener ThePlayerListener = new HPlayerListener(this);
    private final HEntityListener TheEntityListener = new HEntityListener(this);
    private final HRegen TheHRegen = new HRegen(this);
    private Permissions HPermissions = null;
    
	public void onDisable() {
		if (TheHRegen.hActive) {
			TheHRegen.StopHRegen();
		}
		System.out.println("HealthPlugin v" + HVersion + " Disabled");
	}

	public void onEnable() {
		setupPermissions();
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.ThePlayerChatListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.ThePlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.TheEntityListener, Event.Priority.Highest, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + HVersion + " is enabled! (bentzilla)");
		
		//TheHRegen.StartHRegen(0, 0);
	}
	
    private void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
        PluginDescriptionFile pdfFile = this.getDescription();

        if (permissionsPlugin != null) {
           System.out.println(pdfFile.getName() + ": Using Nijikokun's permissions plugin for permissions");
            this.HPermissions = ((Permissions) permissionsPlugin);
        }
    }
    
    public Permissions getPermissions() {
        return this.HPermissions;
    }

	 public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			
		 if (HPermissions == null) {
             sender.sendMessage("Cannot find Permissions plugin.");
             return false;
         }
		 
		 if (!(sender instanceof Player)) {
				return false;
		 }
		 
		 boolean CanUse = HPermissions.Security.permission((Player) sender, "hp.use");
		 if (!CanUse){
             sender.sendMessage("You do not have permission to use this command.");
             return true;
		 }
			
		String command = "/" + commandLabel + " " + join(args, 0);
		
		if (!command.toLowerCase().contains("/hp ")) {
            return false;
        }

        if (args.length < 1) {
            return false;
        }

        Player user = (Player) sender;
        String subCommand = args[0].toLowerCase();

        boolean hasPermission = false;
        boolean hasAdminPermission = false;

        if (subCommand.equals("chathealth") || subCommand.equals("ch")) {
        	
        	hasPermission = HPermissions.Security.permission(user, "hp.chathealth");	

            if (!hasPermission) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            
        	String rpgstyle = "off";
            if (args.length == 2){
            	if (args[1].toLowerCase().equals("on")){
                	rpgstyle = args[1].toLowerCase();
            	}else if(args[1].toLowerCase().equals("off")){
                	rpgstyle = args[1].toLowerCase();
            	}else{
            		user.sendMessage("Example Usage: /hp chathealth on");
            	}
            }else if (args.length == 1){
            	toggleChatHealth(user, rpgstyle);
            	return true;
            }
        	if (!hpChatEnabled(user)){
        		toggleChatHealth(user, rpgstyle);
        	}
        	if (!chatHealthUsers.values().toString().contains(rpgstyle)){
        		Health.chatHealthUsers.remove(user);
        		Health.chatHealthUsers.put(user, rpgstyle);
        	}

        	return true;
        }
        if (subCommand.equals("disablehealth") || subCommand.equals("dh")) {
        	
        	hasPermission = HPermissions.Security.permission(user, "hp.nohealth");
        	hasAdminPermission = HPermissions.Security.permission(user, "hp.nohealth.admin");

            if (!hasPermission) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
           
            if (args.length == 2){
                if (!hasAdminPermission) {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
            	for(Player P : this.getServer().matchPlayer(args[1])){
            		toggleDamageHealth(P);
            		return true;
            	}
            	user.sendMessage("Player: " + args[1].toString() + " not found.");
            	return true;
            }else if (args.length == 1){
            	toggleDamageHealth(user);
            	return true;
            }
        }
        if (subCommand.equals("heal") || subCommand.equals("he")) {
        	
        	hasPermission = HPermissions.Security.permission(user, "hp.heal");
        	hasAdminPermission = HPermissions.Security.permission(user, "hp.heal.admin");

            if (!hasPermission) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
           
            if (args.length == 2){
                if (!hasAdminPermission) {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
            	for(Player P : this.getServer().matchPlayer(args[1])){
            		P.setHealth(20);
            		P.sendMessage("You have been healed by: " + user.getName().toString());
            		return true;
            	}
            	user.sendMessage("Player: " + args[1].toString() + " not found.");
            	return true;
            } else if (args.length == 1){
            	user.setHealth(20);
        		user.sendMessage("You have been healed");
            	return true;
            }
        }
        if (subCommand.equals("harm") || subCommand.equals("ha")) {
        	
        	hasPermission = HPermissions.Security.permission(user, "hp.harm");
        	hasAdminPermission = HPermissions.Security.permission(user, "hp.harm.admin");

            if (!hasPermission) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
           
            if (args.length == 3){
                if (!hasAdminPermission) {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
            	for(Player P : this.getServer().matchPlayer(args[2])){
            		P.damage(Integer.parseInt(args[1]));
            		P.sendMessage("You have been harmed " + args[1] + " hitpoints by: " + user.getName().toString());
            		return true;
            	}
            	user.sendMessage("Player: " + args[2].toString() + " not found.");
            	return true;
            } else if (args.length == 2){
            	user.damage(Integer.parseInt(args[1]));
        		user.sendMessage("You have been harmed " + args[1] + " hitpoints");
            	return true;
            }
        }
        if (subCommand.equals("regen")) {
        	
        	hasPermission = HPermissions.Security.permission(user, "hp.regen.set");

            if (!hasPermission) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
           
            if (args.length == 3){
        		if (TheHRegen.hActive) {
        			TheHRegen.StopHRegen();
        		}
        		TheHRegen.StartHRegen(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            	user.sendMessage("Regenerate " + args[1].toString() + " hitpoints every " + args[2].toString() + " seconds");
            	return true;
            }
        }
        return false;
	 }
	   
	    public static boolean hpChatEnabled(Player player) {
			return chatHealthUsers.containsKey(player);
		}

	    public boolean toggleChatHealth(Player player, String style){
	    	if(hpChatEnabled(player)) {
	    		Health.chatHealthUsers.remove(player);
	    		player.sendMessage("Health in Chat DISABLED");
	    	} else {
	    		Health.chatHealthUsers.put(player, style);
	    		player.sendMessage("Health in Chat ENABLED");
	    	}		
	    	return true;
	    }
	    
	    public static boolean damageHealthEnabled(Player player) {
			return damageHealthUsers.containsKey(player);
		}
	    
	    public boolean toggleDamageHealth(Player player){
	    	if(damageHealthEnabled(player)) {
	    		Health.damageHealthUsers.remove(player);
	    		player.sendMessage("Health ENABLED");
	    	} else {
	    		Health.damageHealthUsers.put(player, null);
	    		player.sendMessage("Health DISABLED");
	    	}		
	    	return true;
	    }
    
    public static String join(String[] arr, int offset) {
		return join(arr, offset, " ");
	}

	/**
	 * Join an array command into a String
	 * @author Hidendra
	 * @param arr
	 * @param offset
	 * @param delim
	 * @return
	 */
	public static String join(String[] arr, int offset, String delim) {
		String str = "";

		if (arr == null || arr.length == 0) {
			return str;
		}

		for (int i = offset; i < arr.length; i++) {
			str += arr[i] + delim;
		}

		return str.trim();
	}
}