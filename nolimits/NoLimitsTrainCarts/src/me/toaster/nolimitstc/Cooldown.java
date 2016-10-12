package me.toaster.nolimitstc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
 
import org.bukkit.entity.Player;
public class Cooldown {
 
    private static Map<String, Cooldown> cooldowns = new HashMap<String, Cooldown>();
    private long start;
    private final int timeInSeconds;
    private final String id;
    private final String cooldownName;
    /**
     * Cooldown for wand/balloon
     * @param id
     * @param cooldownName
     * @param timeInSeconds
     */
    public Cooldown(String id, String cooldownName, int timeInSeconds){
        this.id = id;
        this.cooldownName = cooldownName;
        this.timeInSeconds = timeInSeconds;
    }
    /**
     * Cooldown for wand/balloon
     * @param id
     * @param cooldownName
     * @return
     */
    public static boolean isInCooldown(String id, String cooldownName){
        if(getTimeLeft(id, cooldownName)>=1){
            return true;
        } else {
            stop(id, cooldownName);
            return false;
        }
    }
    /**
     * Stops the cooldown
     * @param id
     * @param cooldownName
     */
    private static void stop(String id, String cooldownName){
        Cooldown.cooldowns.remove(id+cooldownName);
    }
    /**
     * Gets the name of the cooldown
     * @param id
     * @param cooldownName
     * @return
     */
    private static Cooldown getCooldown(String id, String cooldownName){
        return cooldowns.get(id.toString()+cooldownName);
    }
    /**
     * Gets the time left in the cooldown
     * @param id
     * @param cooldownName
     * @return
     */
    public static int getTimeLeft(String id, String cooldownName){
        Cooldown cooldown = getCooldown(id, cooldownName);
        int f = -1;
        if(cooldown!=null){
            long now = System.currentTimeMillis();
            long cooldownTime = cooldown.start;
            int totalTime = cooldown.timeInSeconds;
            int r = (int) (now - cooldownTime) / 1000;
            f = (r - totalTime) * (-1);
        }
        return f;
    }
  
    public void start(){
        this.start = System.currentTimeMillis();
        cooldowns.put(this.id.toString()+this.cooldownName, this);
    }
 
}