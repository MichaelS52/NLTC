package me.toaster.nolimitstc.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.toaster.nolimitstc.Main;
import me.toaster.nolimitstc.ParseUtils;
import me.toaster.nolimitstc.Utils;
import me.toaster.nolimitstc.types.rollercoaster.RollercoasterStartupData;
import me.toaster.nolimitstc.types.rollercoaster.RollercoasterTrain;

public class BasicCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(sender instanceof BlockCommandSender){
			Block blk = ((BlockCommandSender) sender).getBlock();
			if(cmd.getLabel().equalsIgnoreCase("nltcbasic")){
				//nltc <name> <offset> <ID> <loc> <radius> <players>
				if(args.length==7){
					String name = args[0];
					String offset = args[1];
					String block = args[2];
					String loc = args[3];
					String radius = args[4];
					String players = args[5];
					int waitTime = Integer.parseInt(args[6]);
					Location l = ParseUtils.stringToVector(loc).toLocation(blk.getWorld());
					int count = 0;
					ArrayList<Player> pass = Utils.getPlayers(players, l, radius);

					System.out.println(pass.size() + " " + pass);

					double off = Double.parseDouble(offset);
					String[] split = block.split(":");
					int id = Integer.parseInt(split[0]);
					int data = Integer.parseInt(split[1]);

					try {
						RollercoasterStartupData rcdata = new RollercoasterStartupData(name, id, data, off, blk, blk, pass, waitTime);
						RollercoasterTrain train = rcdata.getTrainFromData();
						if(train!=null){
							train.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
						}else{
							System.out.println("Error with NLTC: train is null!");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return false;
	}



}
