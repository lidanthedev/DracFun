package me.lidan.draconic.Other;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Serializer {
    public Object lastObject = null;
    public String laststring = "";

    public Serializer(){

    }

    public void savelast(){
        ErrorFile.get().set("" + (ErrorFile.get().getInt("A-Number") + 1),laststring);
        ErrorFile.get().set("A-Number",ErrorFile.get().getInt("A-Number") + 1);
        ErrorFile.save();
    }

    public String serialize(Object obj){
        String encodedObject = "";
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(obj);
            os.flush();

            byte[] bytes = io.toByteArray();

            encodedObject = Base64.getEncoder().encodeToString(bytes);
        }
        catch(IOException e){
            encodedObject = "ERROR! " + e.getMessage();
        }
        laststring = encodedObject;
        return encodedObject;
    }

    public Object deserialize(String str){
        byte[] serializeedObject = Base64.getDecoder().decode(str);
        Object FixedObject = "ERROR?";
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializeedObject);
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            FixedObject = is.readObject();
        }
        catch(ClassNotFoundException | IOException e){
            FixedObject = "ERROR! " + e;
        }
        lastObject = FixedObject;
        return FixedObject;
    }

    public Object errorize(Object obj){
        String encodedObject = "";
        Object FixedObject = "ERROR?";
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(obj);
            os.flush();

            byte[] bytes = io.toByteArray();

            encodedObject = Base64.getEncoder().encodeToString(bytes);

            byte[] serializeedObject = Base64.getDecoder().decode(encodedObject);


            ByteArrayInputStream in = new ByteArrayInputStream(serializeedObject);
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            FixedObject = is.readObject();

        }
        catch(IOException | ClassNotFoundException e){
            encodedObject = "ERROR! " + e.getMessage();
        }
        return FixedObject;
    }
    public Object supererrorize(Object obj){
        return deserialize(serialize(obj));
    }
}
