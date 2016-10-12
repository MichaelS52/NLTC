package me.toaster.nolimitstc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

public class Train extends BukkitRunnable{

	public Loc getDifference(Loc real, Loc fake){
		return new Loc(real.x-fake.x,real.y-fake.y,real.z-fake.z);
	}

	@Override
	public void run() {
		//

	}

	public void findNearestDetectorRail(Car c, Block track){
		Block top = c.controller.getLocation().clone().add(0, 1, 0).getBlock();
		Block top2 = c.controller.getLocation().clone().add(0, 2, 0).getBlock();

		Block bottom = c.controller.getLocation().clone().add(0, 1, 0).getBlock();
		Block bottom2 = c.controller.getLocation().clone().add(0, 1, 0).getBlock();

		Block rail=null;
		if(top.getType()==track.getType()){
			rail = top;
		}else if(top2.getType()==track.getType()){
			rail = top2;
		}else if(bottom.getType()==track.getType()){
			rail = bottom;
		}else if(bottom2.getType()==track.getType()){
			rail = bottom2;
		}

		if(rail!=null){
			if(rail.getRelative(BlockFace.UP)!=null){
				if(rail.getRelative(BlockFace.UP).getType()==Material.DETECTOR_RAIL){
					if(rail.getRelative(BlockFace.DOWN).getType()!=Material.REDSTONE_BLOCK){
						Material old = rail.getRelative(BlockFace.DOWN).getType();
						rail.getRelative(BlockFace.DOWN).setType(Material.REDSTONE_BLOCK);
						final Block rail1 = rail;
						Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {

							@Override
							public void run() {

								rail1.getRelative(BlockFace.DOWN).setType(old);

							}

						}, 60);

					}

				}
			}
		}
	}
	
	public Block findTrack(Material m, Car c){
		for(int i = 0; i<3; i++){
			Location up = c.controller.getLocation().add(0, i, 0);
			Location down = c.controller.getLocation().subtract(0, i, 0);
			if(up.getBlock().getType()==m){
				return up.getBlock();
			}
			if(down.getBlock().getType()==m){
				return down.getBlock();
			}
		}
		return null;
	}

	public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
	
	public String getCustomName(int cart, String name){
		Random r = new Random();
		int random = r.nextInt(99999);
		return "RC-"+name+"-"+random+"-"+cart;
	}
	
	public CarInfo getInfoAt(int x, ArrayList<String> list){
		x+=1; //add so it doesnt start with LENGTH
		try{
			return getInfo(list.get(x));
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public CarInfo getInfo(String s) {
		String[] parts = s.split(":");
		int num = Integer.parseInt(parts[1]);
		String posOut = parts[2];
		String rotOut = parts[3];

		Loc pos = getLocFromStr(posOut);
		Loc rotation = getLocFromStr(rotOut);
		CarInfo ci = new CarInfo(num, pos, rotation);
		return ci;
	}

	public Car getCarNum(ArrayList<Car> cars, int i){
		for(Car c : cars){
			if(c.num==i){
				return c;
			}
		}
		return null;
	}

	public Loc getLocFromStr(String s){
		String[] st = s.split(",");
		double x = Double.parseDouble(st[1]);
		double y = Double.parseDouble(st[2]);
		double z = Double.parseDouble(st[3]);
		return new Loc(x, y, z);
	}
}
