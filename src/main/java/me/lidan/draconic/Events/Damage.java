package me.lidan.draconic.Events;

import me.lidan.draconic.Draconic;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.LocalDateTime;

public class Damage implements Listener {
    @EventHandler
    public void OnDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();
            Double damage = e.getFinalDamage();
            Double shield = (Double)Draconic.vars.get("shield::" + p.getName());
            Double mshield = (Double)Draconic.vars.get("maxshield::" + p.getName());
            Double ov = (Double)Draconic.vars.get("overload::" + p.getName());
            Double en = (Double)Draconic.vars.get("energy::" + p.getName());
            if (shield + 0 > 0 && !e.isCancelled()) {
                if(ov >= 1d){
                    e.setDamage(damage * (1d + ov/100));
                }
                shield = shield - e.getDamage()*5;
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
