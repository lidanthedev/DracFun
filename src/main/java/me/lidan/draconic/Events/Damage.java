package me.lidan.draconic.Events;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.effect.CircleEffect;
import me.lidan.draconic.Draconic;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.LocalDateTime;

public class Damage implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void OnDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            double multi = 2;
            if (e instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent eb = (EntityDamageByEntityEvent) e;
                if (eb.getDamager().getType() == EntityType.PLAYER)
                    multi = 5;
            }
            Player p = (Player)e.getEntity();
            Double damage = e.getFinalDamage();
            Double shield = (Double)Draconic.vars.get("shield::" + p.getName());
            Double mshield = (Double)Draconic.vars.get("maxshield::" + p.getName());
            Double percentshield = shield/mshield*100;
            Double ov = (Double)Draconic.vars.get("overload::" + p.getName());
            Double en = (Double)Draconic.vars.get("energy::" + p.getName());
            if (shield + 0 > 0 && !e.isCancelled()) {
                if(ov >= 1d){
                    e.setDamage(damage * (1d + ov/100));
                }
                Color particledata = Color.fromRGB(0);
                if (percentshield >= 50){
                    particledata = Color.fromRGB(0, 0, 255);
                }
                else if (percentshield >= 25){
                    particledata = Color.fromRGB(250, 150, 0);
                }
                else {
                    particledata = Color.fromRGB(250, 0, 0);
                }
                // Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0F);
                //  = ((float) dustOptions.getColor().asRGB());
                Draconic.createCircle(p.getLocation().clone().add(0,1,0),1.5f, Particle.REDSTONE,2,particledata);
                shield = shield - e.getDamage()*multi;
                if (shield < 0){
                    shield = 0d;
                }
                else{
                    // e.setCancelled(true);
                    e.setDamage(0);
                    p.setNoDamageTicks(5);
                    float soundPitch = (float) (shield/mshield*1.9f);
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT,0.5f,soundPitch);
                }
                Draconic.vars.put("shield::" + p.getName(),shield);
                Draconic.vars.put("overload::" + p.getName(),ov + 1d + damage*10/mshield);
                double cd = (double)System.currentTimeMillis();
                Draconic.vars.put("overloadcd::" + p.getName(),cd);
                if (Draconic.vars.get("overload::" + p.getName()) > 100d){
                    Draconic.vars.put("overload::" + p.getName(),100d);
                }
                Draconic.dbossbar(p);
            }
        }
    }
}
