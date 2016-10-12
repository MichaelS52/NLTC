package me.toaster.nolimitstc;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;

import net.ess3.api.InvalidWorldException;

public class Utils {

	public static Location findNearestWarp(Location l) {
		Location closest = l.getWorld().getSpawnLocation();

		Warps warps = new Warps(Bukkit.getServer(), Essentials.getPlugin(Essentials.class).getDataFolder());
		for(String str : warps.getList()) {
			try {
				Location loc = warps.getWarp(str);
				if(closest.getWorld().equals(l.getWorld()) && l.getWorld().equals(loc.getWorld())){
					if(l.distance(loc)<l.distance(closest)) {
						closest = loc;
					}
				}
			} catch (WarpNotFoundException | InvalidWorldException e) {
				// TODO Auto-generated catch block
				System.out.println("[NLTC] Warp not found/Invalid world");
			}
		}
		return closest;
	}
	
	public static ArrayList<Player> getPlayers(String players, Location l, String radius){
		ArrayList<Player> pass = new ArrayList<Player>();
		int count = 0;
		for(Player all : Bukkit.getOnlinePlayers()){
			if(count>=Double.parseDouble(players)){
				break;
			}
			if(all.getWorld().equals(l.getWorld())){
				if(all.getLocation().distance(l)<=Double.parseDouble(radius)){
					pass.add(all);
					count++;
				}
			}
		}
		return pass;
	}

	public static boolean doesWarpExist(String warp) {
		Warps warps = new Warps(Bukkit.getServer(), Essentials.getPlugin(Essentials.class).getDataFolder());
		try {
			warps.getWarp(warp);
			return true;
		} catch (WarpNotFoundException | InvalidWorldException e) {
			return false;
		}

	}

	public static String getTrainName(String cartName){
		int lastIndex = cartName.lastIndexOf('-');
		if(lastIndex != -1){
			return cartName.substring(0, lastIndex);
		}else{
			return null;
		}
	}
	
	public static String getRideName(String customName){
		if(customName!=null){
			if(customName.contains("-")){
				String ride = customName.split("-")[1];
				ride = ride.replaceAll(".txt", "");
				return ride;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
}
