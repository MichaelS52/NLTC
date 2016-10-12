package me.toaster.nolimitstc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import me.toaster.nolimitstc.commands.BasicCommand;
import me.toaster.nolimitstc.commands.DoomBuggyCommand;

public class Main extends JavaPlugin implements Listener{

	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("nltcbasic").setExecutor(new BasicCommand());
		getCommand("nltcdoombuggy").setExecutor(new DoomBuggyCommand());
		this.getDataFolder().mkdir();
	}

	public void onDisable(){
		for(World w : Bukkit.getWorlds()){
			for(Entity e : w.getEntities()){
				if(e.hasMetadata("nolimits")){
					e.remove();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void EntityInteract(PlayerInteractAtEntityEvent e){
		if(e.getRightClicked().hasMetadata("nolimits")){
			//e.getRightClicked().setPassenger(e.getPlayer());
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e){
		for(Entity ent : e.getChunk().getEntities()){
			if(ent.hasMetadata("nolimits")){
				ent.remove();
			}
		}
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e){
		if(e.getPlayer().getVehicle()!=null){
			if(e.getPlayer().getVehicle().hasMetadata("nolimits")){
				if(e.getPlayer().getVehicle().getCustomName()!=null){
					String customName = e.getPlayer().getVehicle().getCustomName();
					writeToFile(customName, "player: " + e.getPlayer() + " has executed command: " + e.getMessage());
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDismount(EntityDismountEvent e){
		if(e.getDismounted().hasMetadata("nolimits")){
			e.getEntity().teleport(Utils.findNearestWarp(e.getDismounted().getLocation()));
			e.getEntity().sendMessage(ChatColor.GREEN + "Warped to nearest warp!");
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		if(e.getPlayer().getVehicle()!=null){
			if(e.getPlayer().getVehicle().hasMetadata("nolimits")){
				for(PotionEffect eff : e.getPlayer().getActivePotionEffects()){
					if(e.getPlayer().getVehicle().getCustomName()!=null){
						String customName = e.getPlayer().getVehicle().getCustomName();
						writeToFile(customName, "player: " + e.getPlayer() + " left the server in cart: " + customName);
					}
					e.getPlayer().removePotionEffect(eff.getType());
					e.getPlayer().getVehicle().remove();
					e.getPlayer().teleport(Utils.findNearestWarp(e.getPlayer().getLocation()));
				}
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(p.hasPermission("mcamusement.nltc.check")){
				if(args[0].equalsIgnoreCase("showname")){
					Entity e = getTargetEntity(p);
					if(e!=null){
						String name = e.getCustomName();
						if(name==null) name = "Not found!";
						p.sendMessage(ChatColor.RED + "[NLTC] " + name);
						e.setCustomNameVisible(true);
					}
				}
			}
		}
		return false;
	}

	static Entity getTargetEntity(Entity entity) {
		return getTarget(entity, entity.getWorld().getEntities());
	}

	static <T extends Entity> T getTarget(Entity entity, Iterable<T> entities) {
		T target = null;
		double threshold = 1;
		for (T other:entities) {
			Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
			if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
				if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation().distanceSquared(entity.getLocation()))
					target = other;
			}
		}
		return target;
	}

	public File getFile(String name, String rideName){
		File dir = new File(getDataFolder() + File.separator + "logs" + File.separator + rideName);
		dir.mkdirs();
		File f = new File(dir,name+".txt");
		return f;
	}

	public void writeToFile(String cartName, String msg){
		String trainName = Utils.getTrainName(cartName);
		String rideName = Utils.getRideName(cartName);
		File f = getFile(trainName, rideName);
		try{
			Date d = new Date();
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy - HH:mm");
			BufferedWriter writer = new BufferedWriter(new FileWriter(f,true));
			writer.write(format.format(d) + " - " + msg + "\n");
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public ArrayList<Block> SurroundingBlocks(Block block){
		ArrayList<Block> Blocks = new ArrayList<Block>();
		for (BlockFace face : BlockFace.values()){
			if (face == BlockFace.UP){
				Block above = block.getRelative(BlockFace.UP);
				Block above2 = above.getRelative(BlockFace.UP);
				Blocks.add(above);
				Blocks.add(above2);}
			Blocks.add(block.getRelative(face));
		}
		return Blocks;
	}

	public boolean CheckBlock(Block block){
		if (block.getType() == Material.SIGN_POST){
			return true;
		}
		return false;
	}

}
