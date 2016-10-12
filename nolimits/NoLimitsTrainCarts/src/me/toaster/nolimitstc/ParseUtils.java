package me.toaster.nolimitstc;

import org.bukkit.util.Vector;

public class ParseUtils {

	public static Vector stringToVector(String s){
		if(s.contains(",")){
			String[] parts = s.split(",");
			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);
			double z = Double.parseDouble(parts[2]);
			
			return new Vector(x,y,z);
		}
		
		return null;
	}
	
}
