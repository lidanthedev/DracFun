package me.lidan.draconic.Database;

import me.lidan.draconic.Draconic;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Database {
    public static HashMap<Location,HashMap<String,Object>> lastselectall = null;

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Class.forName("org.h2.Driver");
            // Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection(Draconic.getConnectionUrl());
        }
        catch (SQLException e){
            System.out.println("[Draconic] Database connection error! " + e.getMessage() + "Stack Trace:");
            e.printStackTrace();
        }
        return conn;
    }
    public static void initDatabase(){
        PreparedStatement prepared;
        try {
            /*
            prepared = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Errordata(blockid int, PlayerUUID" +
                    " varchar(200)" +
                    ",BlocksBroken int)");
            prepared.execute();
             */

            prepared = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS BlockData(\n" +
                    "  world varchar(100),\n" +
                    "  x double,\n" +
                    "  y double,\n" +
                    "  z double,\n" +
                    "  btype varchar(16),\n" +
                    "  energy double,\n" +
                    "  maxenergy double,\n" +
                    "  item varchar(1000),\n" +
                    "  placeditem varchar(1000)\n" +
                    ")\n");
            prepared.execute();
        }
        catch (SQLException e){
            System.out.println("[Draconic] Database table create error! " + e.getMessage());
        }
    }
    public static void setblock(Location loc, HashMap<String,Object> blockdata){
        updateblock(loc,blockdata);
    }

    public static void updateblock(Location loc, HashMap<String,Object> blockdata){
        //this is a kinda update [deletes and inserts]
        delete(loc);
        insertblock(loc,blockdata);
    }

    public static void insertblock(Location loc, HashMap<String,Object> blockdata){
        //language=SQL
        String sql = "INSERT INTO BlockData(world,x,y,z,btype,energy,maxenergy,item) VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = getConnection().prepareStatement(sql);
            pstmt.setString(1, loc.getWorld().getName());
            pstmt.setDouble(2,loc.getX());
            pstmt.setDouble(3,loc.getY());
            pstmt.setDouble(4,loc.getZ());
            pstmt.setString(5, (String) blockdata.get("type"));
            pstmt.setDouble(6, 0d);
            pstmt.setDouble(7, 100d);
            String item = Draconic.DracSerializer.serialize(blockdata.get("item"));
            pstmt.setString(8, item);
            pstmt.execute();
        }
        catch (SQLException e){
            System.out.println("[Draconic] Database insert block error! " + e.getMessage());
        }
    }
    public static HashMap<String,Object> select(Location loc){
        HashMap<String,Object> result = new HashMap<>();
        //language=SQL
        String sql =
                "SELECT world, x,y,z,item,btype FROM BlockData WHERE world = '" + loc.getWorld().getName() + "' AND x " +
                        "= " + loc.getX() + " AND y = " + loc.getY() + " AND z = " + loc.getZ();

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs  = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                result.put("world",rs.getString("world"));
                result.put("x",rs.getDouble("x"));
                result.put("y",rs.getDouble("y"));
                result.put("z",rs.getDouble("z"));
                Object itemstringed = Draconic.DracSerializer.deserialize(rs.getString("item"));
                // System.out.println("[DracSer] itemstring= " + itemstringed);
                ItemStack item = (ItemStack) itemstringed;
                result.put("item",item);
                result.put("type",rs.getString("btype"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static void delete(Location loc){
        //language=SQL
        String sql =
                "DELETE FROM BlockData WHERE world = '" + loc.getWorld().getName() + "' AND x " +
                        "= " + loc.getX() + " AND y = " + loc.getY() + " AND z = " + loc.getZ();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void selectAll(){
        lastselectall = null;
        new BukkitRunnable(){
            public HashMap<Location,HashMap<String,Object>> fullresult = new HashMap<>();
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                //language=SQL
                String sql = "SELECT * FROM BlockData";
                try (Connection conn = getConnection();
                     Statement stmt  = conn.createStatement();
                     ResultSet rs    = stmt.executeQuery(sql)){

                    // loop through the result set
                    while (rs.next()) {
                        String world = rs.getString("world");
                        int x = (int) rs.getDouble("x");
                        int y = (int) rs.getDouble("y");
                        int z = (int) rs.getDouble("z");
                        Location locat = new Location(Bukkit.getWorld(world),x, y, z);
                        HashMap<String,Object> result = select(locat);
                        fullresult.put(locat,result);
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                lastselectall = fullresult;

                System.out.println("Finished selectall in " + (System.currentTimeMillis() - now) + "ms");
            }
        }.runTaskAsynchronously(Draconic.getInstance());
    }

    public static void selectAllAndDelete(){
        //language=SQL
        String sql = "SELECT world, x, y,z,item, btype FROM BlockData";
        new BukkitRunnable(){

            @Override
            public void run() {
                ArrayList<Location> deleteafter = new ArrayList<>();
                try (Connection conn = getConnection();
                     Statement stmt  = conn.createStatement();
                     ResultSet rs    = stmt.executeQuery(sql)){

                    // loop through the result set
                    while (rs.next()) {
                        String world = rs.getString("world");
                        String type = rs.getString("btype");
                        int x = (int) rs.getDouble("x");
                        int y = (int) rs.getDouble("y");
                        int z = (int) rs.getDouble("z");
                        Location locat = new Location(Bukkit.getWorld(world),x, y, z);
                        Block blockat = Bukkit.getWorld(world).getBlockAt(locat);
                        if(blockat.getType() == Material.AIR || type == null){
                            System.out.println("[Draconic] block at " + locat.toString() + " was air and got deleted");
                            deleteafter.add(locat);
                            // delete(locat);
                        }
//                        System.out.println(rs.getString("world") +  " " +
//                                rs.getDouble("x") + " " +
//                                rs.getDouble("y") + " " +
//                                rs.getDouble("z"));
                    }
                    Thread.sleep(1000);
                    for (Location locdel: deleteafter) {
                        System.out.println("[Draconic] Deleted " + locdel.toString());
                        delete(locdel);
                        Thread.sleep(100);
                    }
                    selectAll();
                } catch (SQLException | InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }.runTaskAsynchronously(Draconic.getInstance());
    }
}
