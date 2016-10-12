package me.toaster.nolimitstc.types.doombuggy;

import org.bukkit.Bukkit;

import me.toaster.nolimitstc.Car;
import me.toaster.nolimitstc.Main;

public class DoomBuggyUtils {

	public static void addYawOverTime(int ticks, double yaw, Car c){
		double eachIteration = (yaw/ticks)/16;
		final int id = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				c.yaw+=eachIteration;
			}
			
		}, 1, 1).getTaskId();
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(id);
			}
			
		}, ticks);
	}
	
	public static void addPitchOverTime(int ticks, double yaw, Car c){
		double eachIteration = (yaw/ticks);
		final int id = Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				c.pitch+=eachIteration;
			}
			
		}, 1, 1).getTaskId();
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(id);
			}
			
		}, ticks);
	}
}
