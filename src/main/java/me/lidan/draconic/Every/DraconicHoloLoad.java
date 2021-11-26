package me.lidan.draconic.Every;

import me.lidan.draconic.Database.Database;
import me.lidan.draconic.Draconic;
import me.lidan.draconic.Fusion.FusionCrafting;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class DraconicHoloLoad extends BukkitRunnable {
    @Override
    public void run() {
        Database.selectAll();
        while(Database.lastselectall == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HashMap<Location, HashMap<String, Object>> blockData = Database.lastselectall;
        for (HashMap.Entry<Location, HashMap<String, Object>> entry : blockData.entrySet()) {
            double above = 2;
            try {
                assert entry.getValue().get("type") != null;
                if (entry.getValue().get("type").toString().contains("Core")) above = 1;
                double finalAbove = above;
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        FusionCrafting.createFusionHolo(entry.getKey(), finalAbove);
                    }
                }.runTask(Draconic.getInstance());
            }
            catch (Exception error){
                System.out.println("Error happend when loading holograms in " + entry);
                System.out.println(error);
                error.printStackTrace();
            }
        }
    }
}
