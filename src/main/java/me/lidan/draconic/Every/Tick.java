package me.lidan.draconic.Every;

import me.lidan.draconic.Draconic;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static me.lidan.draconic.Draconic.bars;
import static me.lidan.draconic.Draconic.vars;

public class Tick extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()){
            if (vars.get("shield::" + p.getName()) == null){
                vars.put("shield::" + p.getName(),0d);
                vars.put("maxshield::" + p.getName(),0d);
                vars.put("energy::" + p.getName(),0d);
                vars.put("maxenergy::" + p.getName(),0d);
                vars.put("overload::" + p.getName(),0d);
                double cd = (double)System.currentTimeMillis();
                vars.put("overloadcd::" + p.getName(),cd);
                vars.put("up_sr::" + p.getName(),5d);
                vars.put("set::" + p.getName(),0d);
            }
            Double shield = vars.get("shield::" + p.getName());
            Double mshield = vars.get("maxshield::" + p.getName());
            Double ov = vars.get("overload::" + p.getName());
            Double ovcd = vars.get("overloadcd::" + p.getName());
            Double en = vars.get("energy::" + p.getName());
            Double men = vars.get("maxenergy::" + p.getName());
            Double up_sr = vars.get("up_sr::" + p.getName());
            if (bars.get(p) == null) {
                bars.put(p,Bukkit.createBossBar("Draconic",BarColor.BLUE,BarStyle.SOLID));
            }
            if (vars.get("set::" + p.getName()) == 0){
                vars.put("energy::" + p.getName(), (double) Draconic.getArmorEnergyForPlayer(p));
                vars.put("maxenergy::" + p.getName(),(double) Draconic.getMaxArmorEnergyForPlayer(p));
                vars.put("maxshield::" + p.getName(), Draconic.getMaxShieldForPlayer(p));
                vars.put("up_sr::" + p.getName(),Draconic.getRegenShieldForPlayer(p));
                //TODO: if you have draconic buff shield regen
                if (shield > mshield){
                    vars.put("shield::" + p.getName(),mshield);
                }
                if (mshield > 0){
                    bars.get(p).addPlayer(p);
                }
                else{
                    bars.get(p).removePlayer(p);
                }
            }
            if (ov >= 100d){
                up_sr = 0d;
            }
                /*
                if(en + 0 >= 100*up_sr && shield + 0 < mshield + 0){
                    vars.put("shield::" + p.getName(),shield + 0.1d*up_sr);
                    vars.put("energy::" + p.getName(),en - 100d*up_sr);
                    if(shield + 0.1d*up_sr > mshield){
                        vars.put("shield::" + p.getName(),mshield);
                    }
                }
                 */
            if(Draconic.getArmorEnergyForPlayer(p) >= 10*up_sr && shield + 0 < mshield + 0){
                Draconic.removeArmorEnergyForPlayer(p, (float) (10*up_sr));
                vars.put("shield::" + p.getName(),shield + 0.1d*up_sr);
                vars.put("energy::" + p.getName(),en - 10d*up_sr);
                if(shield + 0.1d*up_sr > mshield){
                    vars.put("shield::" + p.getName(),mshield);
                }
            }
            if((double)System.currentTimeMillis() - ovcd >= 5000 && ov > 0){
                vars.put("overload::" + p.getName(),ov-0.1);
                if (vars.get("overload::" + p.getName()) < 0){
                    vars.put("overload::" + p.getName(),0d);
                }
            }
            Draconic.dbossbar(p);

        }
    }
}
