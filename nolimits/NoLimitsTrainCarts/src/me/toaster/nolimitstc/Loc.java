package me.toaster.nolimitstc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;

public class Loc {

	public double x, y, z;
	
	public Loc(double x, double y, double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Loc add(Loc l){
		Loc newLoc = new Loc(this.x+l.x,this.y+l.y,this.z+l.z);
		return newLoc;
	}
	
	public String toString(){
		return "x:"+x +",y:" +y +",z:" + z;
	}
	
	public Location toLocation(World w){
		return new Location(w,this.x,this.y,this.z);
	}
	
	public EulerAngle toEuler(){
		return new EulerAngle(this.x*-1, 0, this.z);
	}
	
	public Loc subtract(Loc l){
		return new Loc(this.x-l.x,this.y-l.y,this.z-l.z);
	}
	
	public Loc add(double x, double y, double z){
		return new Loc(this.x+x,this.y+y,this.z+z);
	}
	
	public String getString(){
		return this.x+","+this.y+","+this.z;
	}
}
