package me.toaster.nolimitstc.types.doombuggy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.toaster.nolimitstc.Car;
import me.toaster.nolimitstc.CarInfo;
import me.toaster.nolimitstc.Loc;
import me.toaster.nolimitstc.Main;
import me.toaster.nolimitstc.ParseUtils;
import me.toaster.nolimitstc.Train;
import me.toaster.nolimitstc.Utils;

public class DoomBuggyTrain extends Train{

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
	public Loc last;
	public Sign lastSign;

	public DoomBuggyTrain(File f, double offset,int backid, int backdata, Block track, Block command, ArrayList<Player> p, int stationwait) throws IOException {
		this.command=command;
		this.stationwait=stationwait;
		this.p=p;
		this.f=f;
		this.track=track.getLocation().add(0, 1, 0).getBlock();
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

			//Add to train array, and apply pos, rot.
			car.applyPosRot(info.pos, info.rot, this.difference,this.offset);
			this.train.add(car);
		}

		main.writeToFile(train.get(0).controller.getCustomName(), "The difference is set to " + this.difference);
		main.writeToFile(train.get(0).controller.getCustomName(), "Train array: " + train);


	}

	public void findNearestSign(Car c){
		Block track = findTrack(this.track.getType(), c);
		if(track!=null){
			Location underTrack = track.getLocation().subtract(0, 1, 0);
			if(underTrack.getBlock().getType()==Material.SIGN_POST){
				System.out.println("SignPost");
				Sign s = (Sign) underTrack.getBlock().getState();
				if(lastSign!=null && s.equals(lastSign)){
					System.out.println("Skipped.");
					return;
				}
				if(s.getLine(0).equalsIgnoreCase("[NLTC]")){
					if(s.getLine(1).equalsIgnoreCase("spin")){
						Float yaw = Float.parseFloat(s.getLine(2));
						Integer ticks = Integer.parseInt(s.getLine(3));
						//System.out.println("spin @ " + yaw + "," + ticks);
						DoomBuggyUtils.addYawOverTime(ticks, yaw, c);
						this.lastSign = s;
					}else if(s.getLine(1).equalsIgnoreCase("enter")){
						Vector loc = ParseUtils.stringToVector(s.getLine(2));
						Location l = loc.toLocation(c.controller.getWorld());
						String[] third = s.getLine(3).split(":");
						ArrayList<Player> pass = Utils.getPlayers(third[0], l, third[1]);
						if(pass.size()>0){
							if(c.controller.getPassenger()==null){
								c.controller.setPassenger(pass.get(0));
								main.writeToFile(c.controller.getCustomName(), "Auto entered passenger " + pass.get(0) + " into car at position " + 0);
							}
						}
						this.lastSign = s;
					}else if(s.getLine(1).equalsIgnoreCase("exit")){
						c.controller.eject();
						this.lastSign = s;
					}else if(s.getLine(1).equalsIgnoreCase("tilt")){
						Float amount = Float.parseFloat(s.getLine(2));
						Integer ticks = Integer.parseInt(s.getLine(3));
						DoomBuggyUtils.addPitchOverTime(ticks, amount, c);
						this.lastSign = s;
					}
				}
			}
		}else{
			System.out.println("Couldn't find track");
		}
	}

	public void applyPosRelativeRot(Loc pos, Loc rot, Loc diff, double offset, Car c){
		if(last==null){
			c.applyPosRot(pos, rot, diff, offset);
			last = rot;
			return;
		}else{
			Loc toAdd = last.subtract(rot);
			c.applyPosRot(pos, toAdd.y, toAdd.x, rot, diff, offset);
			this.last = rot;
		}
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
				findNearestSign(c);

				applyPosRelativeRot(info.pos, info.rot, difference, this.offset, c);

				line++;
			}

		}else{
			for(int i=0; i<this.length; i++){ //Waiting in the station
				CarInfo info = getInfoAt(i, str);
				Car c = train.get(i);
				findNearestDetectorRail(c,this.track);
				findNearestSign(c);
				line++;
				c.applyPosRot(info.pos, info.rot, difference,this.offset);
			}
		}

	}

}
