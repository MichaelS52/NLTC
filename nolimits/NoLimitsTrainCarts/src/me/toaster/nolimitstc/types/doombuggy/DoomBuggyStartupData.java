package me.toaster.nolimitstc.types.doombuggy;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.toaster.nolimitstc.Main;

public class DoomBuggyStartupData {

	String name = "";
	int id = 0, data = 0;
	double offset = 0;
	Block sender;
	Block track;
	ArrayList<Player> pass;
	int stationWait = 0;
	public DoomBuggyStartupData(String name, int id, int data, double offset, Block sender, Block track,
			ArrayList<Player> pass, int stationWait) {
		super();
		this.name = name;
		this.id = id;
		this.data = data;
		this.offset = offset;
		this.sender = sender;
		this.track = track;
		this.pass = pass;
		this.stationWait = stationWait;
	}

	public DoomBuggyTrain getTrainFromData(){
		try{
			File f = new File(Main.getPlugin(Main.class).getDataFolder(),name+".txt");
			if(f.exists()){
				return new DoomBuggyTrain(f, offset, id, data, track, sender, pass, stationWait);
			}else{
				System.out.println("Error file does not exist: " + f);
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getData() {
		return data;
	}
	public void setData(int data) {
		this.data = data;
	}
	public double getOffset() {
		return offset;
	}
	public void setOffset(double offset) {
		this.offset = offset;
	}
	public Block getTrack() {
		return track;
	}
	public void setTrack(Block track) {
		this.track = track;
	}
	public ArrayList<Player> getPass() {
		return pass;
	}
	public void setPass(ArrayList<Player> pass) {
		this.pass = pass;
	}
	public int getStationWait() {
		return stationWait;
	}
	public void setStationWait(int stationWait) {
		this.stationWait = stationWait;
	}

	
}
