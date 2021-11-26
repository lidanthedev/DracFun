package me.lidan.draconic.Events;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.lidan.draconic.Draconic;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Death implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity.getType() == EntityType.ENDER_DRAGON){
            SlimefunItemStack DRAGON_HEART = (SlimefunItemStack) SlimefunItem.getById("DRAGON_HEART").getItem();
            Item item = entity.getWorld().dropItem(entity.getLocation(),DRAGON_HEART);
            Draconic.getInstance().getLogger().info("Ender Dragon Heart at " + entity.getLocation());
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0F);
            new BukkitRunnable(){

                @Override
                public void run() {
                    if (item.isDead()){
                        cancel();
                    }
                    item.getWorld().spawnParticle(Particle.REDSTONE,item.getLocation(),50, 2,2,2,dustOptions);
                }
            }.runTaskTimer(Draconic.getInstance(),0,1);
        }
    }
}
