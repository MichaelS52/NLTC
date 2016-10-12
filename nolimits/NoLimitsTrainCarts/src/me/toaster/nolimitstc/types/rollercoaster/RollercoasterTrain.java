package me.toaster.nolimitstc.types.rollercoaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import me.toaster.nolimitstc.Car;
import me.toaster.nolimitstc.CarInfo;
import me.toaster.nolimitstc.Loc;
import me.toaster.nolimitstc.Main;
import me.toaster.nolimitstc.Train;

public class RollercoasterTrain extends Train{

	public int line = 1;
	public int length = 0;
	public double offset=-1.0, angleOffset = 0;
	public File f;
	Block command;
	ArrayList<String> str = new ArrayList<String>();
	ArrayList<Car> train = new ArrayList<Car>();
	public Loc difference;
	public int back, backdata;
	public Block track;
	public ArrayList<Player> p;
	public int stationwait;
	public int runcount=0;
	public Main main;

	public RollercoasterTrain(File f, double offset,int backid, int backdata, Block track, Block command, ArrayList<Player> p, int stationwait) throws IOException {
		this.command=command;
		this.stationwait=stationwait;
		this.p=p;
		this.f=f;
		this.track=command.getRelative(BlockFace.UP);
		this.back=backid;
		this.backdata=backdata;
		this.offset=offset;
		FileReader reader = new FileReader(this.f);
		BufferedReader br = new BufferedReader(reader);
		String line;
		String initial=null;
		this.main = Main.getPlugin(Main.class);
		int c=0;
		//Add lines when its not the first line, set the first line (length:?) to initial string.
		while ((line = br.readLine()) != null) {
			if(c!=0){
				str.add(line);
			}else{
				initial=line;
			}
			c++;
		}
		br.close();
		//set length to the first lines value
		this.length = Integer.parseInt(initial.split(":")[1]);

		for(int i=0; i<this.length; i++){
			//Get everything from current line.
			CarInfo info = getInfo(str.get(i));
			
			Location real = command.getLocation().add(0, 2, 0);
			
			//Set difference on first iteration
			if(i==0){
				this.difference=getDifference(new Loc(real.getX(),real.getY(),real.getZ()), info.pos);
			}
			//Create car every iteration
			Car car = new Car(info.car, info.pos.add(this.difference), command.getWorld(),info,back,backdata,true,false);
			//While the passenger size is more than iterated car add them into car
			car.controller.setCustomName(getCustomName(i, f.getName()));
			car.controller.setCustomNameVisible(false);
			main.writeToFile(car.controller.getCustomName(), "Created car, in the i position of, " + i);
			
			System.out.println("track: " + this.track);
			
			if(p.size()>i){
				Player play = p.get(i);
				car.controller.setPassenger(play);
				main.writeToFile(car.controller.getCustomName(), "Auto entered passenger " + play + " into car at position " + i);
			}

			//Add to train array, and apply pos, rot.
			car.applyPosRot(info.pos, info.rot, this.difference,this.offset);
			this.train.add(car);
		}
		
		main.writeToFile(train.get(0).controller.getCustomName(), "The difference is set to " + this.difference);
		main.writeToFile(train.get(0).controller.getCustomName(), "Train array: " + train);

		
	}
	
	@Override
	public void run() {

		runcount++;
		if(runcount>=stationwait){
			if(runcount==stationwait){
				line=0;
			}

			for(int i=0; i<=this.length; i++){
				if(i==this.length){
					line++;
					return;
				}
				if(line>=str.size()){
					for(Car c : train){
						c.controller.remove();
					}
					this.cancel();
					return;
				}
				String currLine = str.get(line);
				CarInfo info = getInfo(currLine);
				Car c = train.get(i);
				findNearestDetectorRail(c,this.track);

				c.applyPosRot(info.pos, info.rot, difference,this.offset);
				line++;
			}

		}else{
			for(int i=0; i<this.length; i++){ //Waiting in the station
				CarInfo info = getInfoAt(i, str);
				Car c = train.get(i);
				findNearestDetectorRail(c,this.track);
				line++;
				c.applyPosRot(info.pos, info.rot, difference,this.offset);
			}
		}

	}

}
