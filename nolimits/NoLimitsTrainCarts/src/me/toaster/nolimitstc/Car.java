package me.toaster.nolimitstc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;

public class Car {

	public float yaw = 0;
	public float pitch = 0;
	public ArmorStand controller;
	public int num = 0;
	public Loc old;
	public Loc key;

	public Car(int num, Loc l, World w, CarInfo ci, int i, int data,boolean allowentry, boolean isFront) {
		ArmorStand a = (ArmorStand) w.spawnEntity(new Location(w,l.x,l.y,l.z,(float)Math.toDegrees(ci.rot.y)*-1,0), EntityType.ARMOR_STAND);
		//a.setBasePlate(false);
		if(allowentry){
			a.setMetadata("nolimits", new FixedMetadataValue(Main.getPlugin(Main.class), true));
		}
		a.setHelmet(new ItemStack(i,1,(short)data));
		a.setGravity(false);
		a.setVisible(false);
		this.num=num;
		this.old=l;
		this.controller=a;
	}

	public Loc getCurrentLoc(){
		return new Loc(controller.getLocation().getX(),controller.getLocation().getY(),controller.getLocation().getZ());
	}

	public void teleportController(Location l){
		CraftArmorStand cas = (CraftArmorStand) controller;
		cas.getHandle().setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		//this.controller.setVelocity(l.toVector().subtract(this.controller.getLocation().toVector()));
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(cas.getHandle());

		for(Player all : Bukkit.getOnlinePlayers()){
			CraftPlayer p = (CraftPlayer)all;
			p.getHandle().playerConnection.sendPacket(teleport);
		}
	}

	public void applyPosRot(Loc pos, Loc rot, Loc difference,double offset){
		Location l = pos.add(difference).toLocation(controller.getWorld());
		float yaw = (float) Math.toDegrees(rot.y)*-1;
		l.setYaw(yaw+180);
		this.yaw = yaw+180;
		l.add(0, offset, 0);
		this.teleportController(l);
		EulerAngle eul = rot.toEuler();
		this.controller.setHeadPose(eul);
	}
	
	public void applyPosRot(Loc pos, double toAddYaw, Loc rot, Loc difference,double offset){
		Location l = pos.add(difference).toLocation(controller.getWorld());
		yaw+=Math.toDegrees(toAddYaw);
		l.setYaw(yaw);
		l.add(0, offset, 0);
		this.teleportController(l);
		EulerAngle eul = rot.toEuler();
		this.controller.setHeadPose(eul);
	}
	
	public void applyPosRot(Loc pos, double toAddYaw, double toAddPitch, Loc rot, Loc difference,double offset){
		Location l = pos.add(difference).toLocation(controller.getWorld());
		this.yaw+=Math.toDegrees(toAddYaw);
		this.pitch+=Math.toDegrees(toAddPitch);
		l.setYaw(yaw);
		l.add(0, offset, 0);
		this.teleportController(l);
		EulerAngle eul = rot.toEuler();
		EulerAngle wPitch = new EulerAngle(Math.toRadians(this.pitch), eul.getY(), eul.getZ());
		this.controller.setHeadPose(wPitch);
	}
}
