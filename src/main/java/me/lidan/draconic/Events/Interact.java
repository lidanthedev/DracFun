package me.lidan.draconic.Events;

import me.lidan.draconic.Database.Database;
import me.lidan.draconic.Draconic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Date;
import java.util.HashMap;

public class Interact implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        String iname = "";
        try{
            iname = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        }
        catch (Exception ERROR){
            iname = p.getInventory().getItemInMainHand().getType().toString();
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && p.getName().contains("LidanTheGamer") && iname.contains("Fusion")){
            // p.sendMessage(e.getBlockFace().toString());
            String finalIname = iname;
            new BukkitRunnable(){
                @Override
                public void run() {
                    RayTraceResult ray = p.getWorld().rayTraceBlocks(p.getEyeLocation(),
                            p.getEyeLocation().getDirection(),
                            20d);
                    if (ray != null){
                        BlockFace face = ray.getHitBlockFace();
                        Block block = ray.getHitBlock();
                        p.sendMessage("rayed " + face);
                        // Entity zimon = p.getWorld().spawnEntity(block.getLocation(), EntityType.ZOMBIE);
                        // ItemFrame i = (ItemFrame) p.getWorld().spawnEntity(block.getLocation().add(0.5,0.5,0.5),
                                // EntityType.ITEM_FRAME);
                        // ItemFrame i = Draconic.SpawnItemFrame(block.getLocation(),face);
                        // i.setInvulnerable(true);
                        // i.setFixed(true);
                        HashMap<String,Object> blockdata = new HashMap<>();
                        blockdata.put("type",finalIname);
                        blockdata.put("energy",0d);
                        blockdata.put("maxenergy",0d);
                        blockdata.put("item",new ItemStack(Material.AIR));
                        // Draconic.blockdata.put(block.getLocation(), blockdata);
                        Database.setblock(block.getLocation(), blockdata);
                        // i.setVisible(false);
                        /*
                        i.setFacingDirection(BlockFace.SOUTH);
                        HangingPlaceEvent hEvent = new HangingPlaceEvent(i, p, block, face);
                        Draconic.getInstance().getServer().getPluginManager().callEvent(hEvent);
                         */
                    }
                    else p.sendMessage("ERROR?");
                }
            }.runTaskLater(Draconic.getInstance(), 1L);
        }
    }

    /*
    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        Block b = event.getBlock();
        b.setMetadata("PLACED", new FixedMetadataValue(Draconic.getInstance(),
                "Placed by: " + event.getPlayer().getUniqueId()));
    }
     */

}
