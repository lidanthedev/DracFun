package me.lidan.draconic.Commands;

import com.google.common.base.Predicate;
import me.lidan.draconic.Draconic;
import me.lidan.draconic.Other.aiflyrun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class aiflycmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("ERROR! must be player");
            return true;
        }
        Player p = (Player)sender;
        if (args.length > 1){
            if (args[0].equalsIgnoreCase("target")){
                Double raysize = Double.parseDouble(args[1]);
                Predicate<Entity> filter = i -> (i != p);
                RayTraceResult ray = p.getWorld().rayTraceEntities(p.getEyeLocation(),
                        p.getEyeLocation().getDirection(),
                        20d,1,filter);
                if (ray == null){
                    p.sendMessage("Yeany did an error!");
                    return true;
                }
                if(ray.getHitEntity() == null){
                    p.sendMessage("Maxi did an error!");
                    return true;
                }
                Entity e = ray.getHitEntity();
                p.sendMessage("Fly! " + e.getName());
                new aiflyrun(e,p.getLocation(),1000).runTaskTimer(Draconic.getInstance(),0,1);
            }
        }
        return true;
    }

}
