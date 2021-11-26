package me.lidan.draconic;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.mooy1.infinitylib.commands.AddonCommand;
import io.github.mooy1.infinitylib.common.CoolDowns;
import io.github.mooy1.infinitylib.common.Scheduler;
import io.github.mooy1.infinitylib.core.AbstractAddon;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ChargingBench;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.lidan.draconic.Commands.aiflycmd;
import me.lidan.draconic.Commands.DraconicCmd;
import me.lidan.draconic.Database.Database;
import me.lidan.draconic.Events.Damage;
import me.lidan.draconic.Events.Death;
import me.lidan.draconic.Events.Interact;
import me.lidan.draconic.Every.DraconicHoloLoad;
import me.lidan.draconic.Every.Tick;
import me.lidan.draconic.Fusion.FusionCrafting;
import me.lidan.draconic.Other.ElectroBlock;
import me.lidan.draconic.Other.ErrorFile;
import me.lidan.draconic.Other.Serializer;
import me.lidan.draconic.Other.DraconicArmorPiece;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;

public final class Draconic extends AbstractAddon {
    // private static Draconic instance = null;
    private static String connectionUrl;
    public static HashMap<Player,BossBar> bars = new HashMap<>();
    // public static HashMap<Location,HashMap<String,Object>> blockdata = new HashMap<>();
    public static HashMap<String,Double> vars = new HashMap<>();
    public static HashMap<String,Object> allvars = new HashMap<>();
    public static Tick everytick = new Tick();
    public static DraconicHoloLoad every5secs = new DraconicHoloLoad();
    public static Serializer DracSerializer = new Serializer();
    // public static BossBar bar = Bukkit.createBossBar("Test", BarColor.BLUE, BarStyle.SOLID);
    public static ItemGroup DraconicGroup;

    public Draconic() {
        super("LidanTheDev", "repo", "master", "no");
    }

    public static Draconic getInstance() {
        final Draconic i = instance();
        if (i == null)
            throw new IllegalStateException("Instance is not created!");
        return i;
    }

    public static String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public void enable() {
        // Plugin startup logic
        Config cfg = new Config(this);
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }
        connectionUrl = "jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/data.db";
        Database.initDatabase();
        getServer().getPluginManager().registerEvents(new Damage(),this);
        getServer().getPluginManager().registerEvents(new Interact(),this);
        getServer().getPluginManager().registerEvents(new FusionCrafting(),this);
        getServer().getPluginManager().registerEvents(new Death(),this);
        everytick.runTaskTimer(this,0L,5L);
        every5secs.runTaskAsynchronously(this);
        getCommand("aiflyto").setExecutor(new aiflycmd());
        getCommand("draconic").setExecutor(new DraconicCmd());

        //arry = {core,fused,injector,+8 items} 11 size 10 length
        FusionCrafting.addRecipe("test", new ItemStack[]{new ItemStack(Material.IRON_BLOCK),
                new ItemStack(Material.DIAMOND_BLOCK),FusionCrafting.getItemInjectortier(1),
                new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND),new ItemStack(Material.DIAMOND)});
        FusionCrafting.addRecipe("test2", new ItemStack[]{new ItemStack(Material.DIAMOND_BLOCK),
                new ItemStack(Material.EMERALD_BLOCK),FusionCrafting.getItemInjectortier(1),
                new ItemStack(Material.EMERALD),new ItemStack(Material.EMERALD),new ItemStack(Material.EMERALD),
                new ItemStack(Material.EMERALD),new ItemStack(Material.EMERALD),new ItemStack(Material.EMERALD),
                new ItemStack(Material.AIR),new ItemStack(Material.AIR)});
        FusionCrafting.addRecipe("test3", new ItemStack[]{new ItemStack(Material.NETHERITE_CHESTPLATE),
                new ItemStack(Material.ELYTRA),FusionCrafting.getItemInjectortier(4),
                new ItemStack(Material.SHULKER_SHELL),new ItemStack(Material.SHULKER_SHELL),
                new ItemStack(Material.LEATHER),
                new ItemStack(Material.LEATHER),new ItemStack(Material.END_CRYSTAL),new ItemStack(Material.END_CRYSTAL),
                new ItemStack(Material.AIR),new ItemStack(Material.AIR)},10000000);
        FusionCrafting.addRecipe("test4", new ItemStack[]{SlimefunItems.NUCLEAR_REACTOR,
                new ItemStack(SlimefunItems.NETHER_STAR_REACTOR),FusionCrafting.getItemInjectortier(4),
                new ItemStack(Material.NETHER_STAR),new ItemStack(Material.NETHER_STAR),
                new ItemStack(Material.NETHER_STAR),
                new ItemStack(Material.NETHER_STAR),new ItemStack(Material.END_CRYSTAL),
                new ItemStack(Material.END_CRYSTAL),
                new ItemStack(Material.SOUL_SAND),new ItemStack(Material.SOUL_SAND)},1000000000);
        ErrorFile.setup();
        ErrorFile.get().addDefault("A-Number",0);
        // ErrorFile.get().addDefault("Overload-Nerf",0d);
        // ErrorFile.get().set("Bigerror","YES");
        ErrorFile.get().options().copyDefaults(true);
        ErrorFile.save();

        /*
        if (Slimefun.instance() != null)
            setup();
        else{
            getLogger().log(Level.WARNING,"Error Slimefun instance was null slimefun items didn't load!");
        }
         */
        try {
            setup();
        } catch (Exception error){
            getLogger().severe("Error happened when loading items");
            error.printStackTrace();
        }

        System.out.println("Draconic Evolution Loaded");
    }

    @Override
    public void disable() {
        // Plugin shutdown logic
        // everytick.cancel();
        Bukkit.getScheduler().cancelTasks(this);
        for(Player p : Bukkit.getOnlinePlayers()){
            if (bars.get(p) != null){
                bars.get(p).removeAll();
            }
        }
        for (Hologram holo: HologramsAPI.getHolograms(this)) {
            holo.delete();
        }
        Slimefun.getRegistry().getAllItemGroups().remove(DraconicGroup);
        System.out.println("Draconic Evolution Unloaded");
    }

    public void setup(){
        NamespacedKey categoryId = new NamespacedKey(this, "Draconic");
        ItemStack categoryItem = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = categoryItem.getItemMeta();
        meta.setDisplayName("§6§lDraconic");
        categoryItem.setItemMeta(meta);
        DraconicGroup = new ItemGroup(categoryId, categoryItem);
        DraconicGroup.register(this);
        ItemStack endcrystal = new ItemStack(Material.END_CRYSTAL);
        Color WyvernColor = Color.fromRGB(227, 3, 252);
        if(SlimefunItem.getById("WYVERN_HELMET") != null){
            Slimefun.getRegistry().getSlimefunItemIds().remove("WYVERN_HELMET");
            Slimefun.getRegistry().getSlimefunItemIds().remove("WYVERN_CHESTPLATE");
            Slimefun.getRegistry().getSlimefunItemIds().remove("WYVERN_LEGGINGS");
            Slimefun.getRegistry().getSlimefunItemIds().remove("WYVERN_BOOTS");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRACONIC_INJECTOR");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRACONIC_ENERGY_INFUSER");
            Slimefun.getRegistry().getSlimefunItemIds().remove("CHAOTIC_INJECTOR");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRACONIC_OP_ENERGY");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRACONIUM_INGOT");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRACONIC_CORE");
            Slimefun.getRegistry().getSlimefunItemIds().remove("WYVERN_CORE");
            Slimefun.getRegistry().getSlimefunItemIds().remove("WYVERN_ENERGY_CORE");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRACONIUM_BLOCK");
            Slimefun.getRegistry().getSlimefunItemIds().remove("DRAGON_HEART");
            Slimefun.getRegistry().getSlimefunItemIds().remove("BASIC_INJECTOR");
        }


        if(SlimefunItem.getById("DRACONIUM_INGOT") == null){
            SlimefunItemStack draconium_ingot = new SlimefunItemStack("DRACONIUM_INGOT", Material.PURPLE_DYE,
                    "&dDraconium Ingot",
                    "&7Used to craft draconic items");
            ItemStack[] recipe = {
                    new ItemStack(Material.POPPED_CHORUS_FRUIT),null,null,
                    null,null,null,
                    null,null,null
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, draconium_ingot,RecipeType.SMELTERY,
                    recipe);
            slimeitem.register(this);
        }
        SlimefunItemStack DRACONIUM_INGOT = (SlimefunItemStack) SlimefunItem.getById("DRACONIUM_INGOT").getItem();

        if(SlimefunItem.getById("DRACONIUM_BLOCK") == null){
            SlimefunItemStack draconium_block = new SlimefunItemStack("DRACONIUM_BLOCK", Material.PURPUR_BLOCK,
                    "&dDraconium Block",
                    "&7Used to craft draconic items");
            ItemStack[] recipe = {
                    DRACONIUM_INGOT, DRACONIUM_INGOT,DRACONIUM_INGOT,
                    DRACONIUM_INGOT,DRACONIUM_INGOT,DRACONIUM_INGOT,
                    DRACONIUM_INGOT,DRACONIUM_INGOT,DRACONIUM_INGOT
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, draconium_block,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe);
            slimeitem.register(this);
            /*
            SlimefunItem slimeitem2 = new SlimefunItem(DraconicGroup, DRACONIUM_INGOT,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe2);
            slimeitem2.register(this);
             */
        }
        SlimefunItemStack DRACONIUM_BLOCK = (SlimefunItemStack) SlimefunItem.getById("DRACONIUM_BLOCK").getItem();
        ItemStack[] recipe2 = {
                DRACONIUM_BLOCK,null,null,
                null,null,null,
                null,null,null
        };
        SlimefunItemStack DRACONIUM_INGOT_9 = (SlimefunItemStack) SlimefunItem.getById("DRACONIUM_INGOT").getItem();
        DRACONIUM_INGOT_9 = (SlimefunItemStack) DRACONIUM_INGOT_9.clone();
        DRACONIUM_INGOT_9.setAmount(9);
        RecipeType.ENHANCED_CRAFTING_TABLE.register(recipe2,DRACONIUM_INGOT_9);

        if(SlimefunItem.getById("DRACONIC_CORE") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("DRACONIC_CORE", Material.BLUE_DYE,
                    "&bDraconic Core",
                    "&7Used to craft draconic items");
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,new ItemStack(Material.GOLD_INGOT),DRACONIUM_INGOT,
                    new ItemStack(Material.GOLD_INGOT),new ItemStack(Material.DIAMOND),new ItemStack(Material.GOLD_INGOT),
                    DRACONIUM_INGOT,new ItemStack(Material.GOLD_INGOT),DRACONIUM_INGOT
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe);
            slimeitem.register(this);
        }
        SlimefunItemStack DRACONIC_CORE = (SlimefunItemStack) SlimefunItem.getById("DRACONIC_CORE").getItem();

        if(SlimefunItem.getById("WYVERN_CORE") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_CORE", Material.PINK_DYE,
                    "&dWyvern Core",
                    "&7Used to craft draconic items");
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,DRACONIC_CORE,DRACONIUM_INGOT,
                    DRACONIC_CORE,new ItemStack(Material.NETHER_STAR),DRACONIC_CORE,
                    DRACONIUM_INGOT,DRACONIC_CORE,DRACONIUM_INGOT
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe);
            slimeitem.register(this);
        }
        SlimefunItemStack WYVERN_CORE = (SlimefunItemStack) SlimefunItem.getById("WYVERN_CORE").getItem();

        if(SlimefunItem.getById("WYVERN_ENERGY_CORE") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_ENERGY_CORE", Material.MAGENTA_DYE,
                    "&dWyvern Energy Core",
                    "&7Used to craft draconic items");
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,new ItemStack(Material.REDSTONE_BLOCK),DRACONIUM_INGOT,
                    new ItemStack(Material.REDSTONE_BLOCK),DRACONIC_CORE,new ItemStack(Material.REDSTONE_BLOCK),
                    DRACONIUM_INGOT,new ItemStack(Material.REDSTONE_BLOCK),DRACONIUM_INGOT
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe);
            slimeitem.register(this);
        }
        SlimefunItemStack WYVERN_ENERGY_CORE = (SlimefunItemStack) SlimefunItem.getById("WYVERN_ENERGY_CORE").getItem();

        if(SlimefunItem.getById("DRAGON_HEART") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("DRAGON_HEART", Material.RED_DYE,
                    "&6Dragon Heart",
                    "&7Used to craft draconic items");
            ItemStack[] recipe = {
                    null,null,null,
                    null,null,null,
                    null,null,null
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, itemStack,RecipeType.NULL,
                    recipe);
            slimeitem.register(this);
        }
        SlimefunItemStack DRAGON_HEART = (SlimefunItemStack) SlimefunItem.getById("DRAGON_HEART").getItem();



        if(SlimefunItem.getById("WYVERN_HELMET") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_HELMET", Material.LEATHER_HELMET,
                    "&dWyvern Helmet",
                    "&6Upgrades:", "&3J Capacity &6-", "&3Shield Capacity &6-", "&3Shield Recovery &6-","",
                    LoreBuilder.powerCharged(0,400000), "", "&6Item Ability: Shield", "&7Uses Electric Power to " +
                    "generate", "&7a shield that protects you", "&7against most attacks");
            LeatherArmorMeta lch = (LeatherArmorMeta)itemStack.getItemMeta();
            lch.setColor(WyvernColor);
            lch.setUnbreakable(true);
            itemStack.setItemMeta(lch);
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,WYVERN_CORE,DRACONIUM_INGOT,
                    DRACONIUM_INGOT,new ItemStack(Material.DIAMOND_HELMET),DRACONIUM_INGOT,
                    DRACONIUM_INGOT,WYVERN_ENERGY_CORE,DRACONIUM_INGOT
            };
            DraconicArmorPiece WyvernHelmet = new DraconicArmorPiece(DraconicGroup, itemStack, recipe,400000f,38);
            WyvernHelmet.register(this);
        }

        if(SlimefunItem.getById("WYVERN_CHESTPLATE") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_CHESTPLATE", Material.LEATHER_CHESTPLATE,
                    "&dWyvern Chestplate",
                    "&6Upgrades:", "&3J Capacity &6-", "&3Shield Capacity &6-", "&3Shield Recovery &6-","",
                    LoreBuilder.powerCharged(0,400000), "", "&6Item Ability: Shield", "&7Uses Electric Power to " +
                    "generate", "&7a shield that protects you", "&7against most attacks");
            LeatherArmorMeta lch = (LeatherArmorMeta)itemStack.getItemMeta();
            lch.setColor(WyvernColor);
            lch.setUnbreakable(true);
            itemStack.setItemMeta(lch);
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,WYVERN_CORE,DRACONIUM_INGOT,
                    DRACONIUM_INGOT,new ItemStack(Material.DIAMOND_CHESTPLATE),DRACONIUM_INGOT,
                    DRACONIUM_INGOT,WYVERN_ENERGY_CORE,DRACONIUM_INGOT
            };
            DraconicArmorPiece WyvernChestplate = new DraconicArmorPiece(DraconicGroup, itemStack, recipe,400000f,102);
            WyvernChestplate.register(this);
        }
        if(SlimefunItem.getById("WYVERN_LEGGINGS") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_LEGGINGS", Material.LEATHER_LEGGINGS,
                    "&dWyvern Leggings",
                    "&6Upgrades:", "&3J Capacity &6-", "&3Shield Capacity &6-", "&3Shield Recovery &6-","",
                    LoreBuilder.powerCharged(0,400000), "", "&6Item Ability: Shield", "&7Uses Electric Power to " +
                    "generate", "&7a shield that protects you", "&7against most attacks");
            LeatherArmorMeta lch = (LeatherArmorMeta)itemStack.getItemMeta();
            lch.setColor(WyvernColor);
            lch.setUnbreakable(true);
            itemStack.setItemMeta(lch);
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,WYVERN_CORE,DRACONIUM_INGOT,
                    DRACONIUM_INGOT,new ItemStack(Material.DIAMOND_LEGGINGS),DRACONIUM_INGOT,
                    DRACONIUM_INGOT,WYVERN_ENERGY_CORE,DRACONIUM_INGOT
            };
            DraconicArmorPiece WyvernLeggings = new DraconicArmorPiece(DraconicGroup, itemStack, recipe,400000f,76);
            WyvernLeggings.register(this);
        }
        if(SlimefunItem.getById("WYVERN_BOOTS") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_BOOTS", Material.LEATHER_BOOTS,
                    "&dWyvern Boots",
                    "&6Upgrades:", "&3J Capacity &6-", "&3Shield Capacity &6-", "&3Shield Recovery &6-","",
                    LoreBuilder.powerCharged(0,400000), "", "&6Item Ability: Shield", "&7Uses Electric Power to " +
                    "generate", "&7a shield that protects you", "&7against most attacks");
            LeatherArmorMeta lch = (LeatherArmorMeta)itemStack.getItemMeta();
            lch.setColor(WyvernColor);
            lch.setUnbreakable(true);
            itemStack.setItemMeta(lch);
            ItemStack[] recipe = {
                    DRACONIUM_INGOT,WYVERN_CORE,DRACONIUM_INGOT,
                    DRACONIUM_INGOT,new ItemStack(Material.DIAMOND_BOOTS),DRACONIUM_INGOT,
                    DRACONIUM_INGOT,WYVERN_ENERGY_CORE,DRACONIUM_INGOT
            };
            DraconicArmorPiece WyvernBoots = new DraconicArmorPiece(DraconicGroup, itemStack, recipe,400000f,38);
            WyvernBoots.register(this);
        }



        if(SlimefunItem.getById("DRACONIC_ENERGY_INFUSER") == null) {
            SlimefunItemStack DRACONIC_ENERGY_INFUSER = new SlimefunItemStack(
                    "DRACONIC_ENERGY_INFUSER",
                    Material.DRAGON_HEAD,
                    "&6Draconic Energy Infuser",
                    "&7Quickly charges items",
                    "&8⇨ &e⚡ &750,000 J Per Second",
                    "&8⇨ &e⚡ &7Energy Loss: &c50%"
            );
            new ChargingBench(DraconicGroup,DRACONIC_ENERGY_INFUSER , RecipeType.NULL, new ItemStack[] {
                    DRACONIUM_INGOT, DRACONIUM_INGOT, DRACONIUM_INGOT,
                    DRACONIUM_INGOT, SlimefunItems.CHARGING_BENCH, DRACONIUM_INGOT,
                    DRACONIUM_INGOT, DRACONIUM_INGOT, DRACONIUM_INGOT,
            }).setCapacity(10000).setEnergyConsumption(10000).setProcessingSpeed(30).register(this);
        }

        if(SlimefunItem.getById("DRACONIC_OP_ENERGY") == null) {
            SlimefunItemStack DRACONIC_OP_ENERGY = new SlimefunItemStack(
                    "DRACONIC_OP_ENERGY",
                    Material.END_ROD,
                    "&6Draconic Energy Generator",
                    "&7Quickly Generate energy",
                    "&8⇨ &e⚡ &71,000,000 J Per Second"
            );
            new SolarGenerator(DraconicGroup,1000000,1000000,DRACONIC_OP_ENERGY , RecipeType.ENHANCED_CRAFTING_TABLE,
                    new ItemStack[] {
                    endcrystal, endcrystal, endcrystal,
                    endcrystal, SlimefunItems.SOLAR_GENERATOR_4, endcrystal,
                    endcrystal, endcrystal, endcrystal,
            }).register(this);
        }
        if(SlimefunItem.getById("FUSION_CORE") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("FUSION_CORE", Material.GLASS,
                    "&bFusion Crafting Core",
                    "&9Fusion Crafting Injector");
            ItemStack[] recipe = {
                    new ItemStack(Material.LAPIS_BLOCK),new ItemStack(Material.DIAMOND), new ItemStack(Material.LAPIS_BLOCK),
                    new ItemStack(Material.DIAMOND),DRACONIC_CORE,new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.LAPIS_BLOCK),new ItemStack(Material.DIAMOND),new ItemStack(Material.LAPIS_BLOCK)
            };
            SlimefunItem slimeitem = new SlimefunItem(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe);
            slimeitem.register(this);
        }
        if(SlimefunItem.getById("BASIC_INJECTOR") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("BASIC_INJECTOR", Material.IRON_BLOCK,
                    "&fBasic Fusion Injector",
                    "&9Fusion Crafting Injector");
            ItemStack[] recipe = {
                    new ItemStack(Material.DIAMOND),DRACONIC_CORE,new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.STONE),new ItemStack(Material.IRON_BLOCK),new ItemStack(Material.STONE),
                    new ItemStack(Material.STONE),new ItemStack(Material.STONE),new ItemStack(Material.STONE)
            };
            ElectroBlock slimeitem = new ElectroBlock(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe,100000);
            slimeitem.register(this);
        }
        if(SlimefunItem.getById("DRACONIC_INJECTOR") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("DRACONIC_INJECTOR", Material.ORANGE_TERRACOTTA,
                    "&6Draconic Fusion Injector",
                    "&9Fusion Crafting Injector");
            ItemStack[] recipe = {
                    endcrystal,endcrystal,endcrystal,
                    endcrystal,endcrystal,endcrystal,
                    endcrystal,endcrystal,endcrystal
            };
            ElectroBlock slimeitem = new ElectroBlock(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe,400000);
            slimeitem.register(this);
        }
        if(SlimefunItem.getById("CHAOTIC_INJECTOR") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("CHAOTIC_INJECTOR", Material.END_PORTAL_FRAME,
                    "&8Chaotic Fusion Injector",
                    "&9Fusion Crafting Injector");
            ItemStack[] recipe = {
                    endcrystal,endcrystal,endcrystal,
                    endcrystal,SlimefunItem.getById("DRACONIC_INJECTOR").getItem(),endcrystal,
                    endcrystal,endcrystal,endcrystal
            };
            ElectroBlock slimeitem = new ElectroBlock(DraconicGroup, itemStack,FusionCrafting.TYPE,
                    recipe,500000);
            slimeitem.register(this);
        }
        if(SlimefunItem.getById("BASIC_INJECTOR") == null){
            SlimefunItemStack itemStack = new SlimefunItemStack("BASIC_INJECTOR", Material.IRON_BLOCK,
                    "&FBasic Fusion Injector",
                    "&9Fusion Crafting Injector");
            ItemStack[] recipe = {
                    endcrystal,endcrystal,endcrystal,
                    endcrystal,new ItemStack(Material.IRON_BLOCK),endcrystal,
                    endcrystal,endcrystal,endcrystal
            };
            ElectroBlock slimeitem = new ElectroBlock(DraconicGroup, itemStack,RecipeType.ENHANCED_CRAFTING_TABLE,
                    recipe,400000);
            slimeitem.register(this);
        }
    }

    public static float getArmorEnergy(ItemStack item){
        float energy = 0;
        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
        if (slimefunItem instanceof Rechargeable) {
            Rechargeable rechargeableItem = (Rechargeable) slimefunItem;
            energy += rechargeableItem.getItemCharge(item);
        }
        return energy;
    }

    public static boolean removeArmorEnergy(ItemStack item,float charge){
        boolean worked = false;
        try {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
            if (slimefunItem instanceof Rechargeable) {
                Rechargeable rechargeableItem = (Rechargeable) slimefunItem;
                worked = rechargeableItem.removeItemCharge(item, charge);
            }
        } catch (IllegalArgumentException e){
            System.out.println("Error in removeArmorEnergy" + item);
            worked = false;
        }
        return worked;
    }

    public static float getArmorMaxEnergy(ItemStack item){
        float energy = 0;
        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
        if (slimefunItem instanceof Rechargeable) {
            Rechargeable rechargeableItem = (Rechargeable) slimefunItem;
            energy += rechargeableItem.getMaxItemCharge(item);
        }
        return energy;
    }

    public static double getArmorMaxShield(ItemStack item){
        double shield = 0;
        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
        if (slimefunItem instanceof DraconicArmorPiece) {
            DraconicArmorPiece dracpiece = (DraconicArmorPiece) slimefunItem;
            shield += dracpiece.shield;
        }
        return shield;
    }

    public static boolean removeArmorEnergyForPlayer(Player p,float charge){
        if (removeArmorEnergy(p.getInventory().getHelmet(),charge)) return true;
        if (removeArmorEnergy(p.getInventory().getChestplate(),charge)) return true;
        if (removeArmorEnergy(p.getInventory().getLeggings(),charge)) return true;
        if (removeArmorEnergy(p.getInventory().getBoots(),charge)) return true;

        return false;
    }

    public static float getArmorEnergyForPlayer(Player p){
        float energy = 0;
        energy += getArmorEnergy(p.getInventory().getHelmet());
        energy += getArmorEnergy(p.getInventory().getChestplate());
        energy += getArmorEnergy(p.getInventory().getLeggings());
        energy += getArmorEnergy(p.getInventory().getBoots());
        return energy;
    }

    public static float getMaxArmorEnergyForPlayer(Player p){
        float energy = 0;
        energy += getArmorMaxEnergy(p.getInventory().getHelmet());
        energy += getArmorMaxEnergy(p.getInventory().getChestplate());
        energy += getArmorMaxEnergy(p.getInventory().getLeggings());
        energy += getArmorMaxEnergy(p.getInventory().getBoots());
        return energy;
    }

    public static double getMaxShieldForPlayer(Player p){
        double energy = 0;
        energy += getArmorMaxShield(p.getInventory().getHelmet());
        energy += getArmorMaxShield(p.getInventory().getChestplate());
        energy += getArmorMaxShield(p.getInventory().getLeggings());
        energy += getArmorMaxShield(p.getInventory().getBoots());
        return energy;
    }

    public static void actionbar(Player p,String msg){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    public static void dactionbar(Player p){
        DecimalFormat numberFormat = new DecimalFormat("0.00");
        Double shield = (Double)vars.get("shield::" + p.getName());
        Double mshield = (Double)vars.get("maxshield::" + p.getName());
        Double ov = (Double)vars.get("overload::" + p.getName());
        Double en = (Double)vars.get("energy::" + p.getName());
        Double men = (Double)vars.get("maxenergy::" + p.getName());
        actionbar(p,
                "§b" + numberFormat.format(shield) + "/" + numberFormat.format(mshield) + " §c" + numberFormat.format(ov) + "%" + " " +
                "§a" + (BigNumber(en)) + "/" + (BigNumber(men)) + " J");
    }

    public static void dbossbar(Player p){
        DecimalFormat numberFormat = new DecimalFormat("0.00");
        Double shield = (Double)vars.get("shield::" + p.getName());
        Double mshield = (Double)vars.get("maxshield::" + p.getName());
        Double ov = (Double)vars.get("overload::" + p.getName());
        Double en = (Double)vars.get("energy::" + p.getName());
        Double men = (Double)vars.get("maxenergy::" + p.getName());
        bars.get(p).setTitle(
                "§b" + numberFormat.format(shield) + "/" + numberFormat.format(mshield) + " §c" + numberFormat.format(ov) + "%" + " " +
                        "§a" + (BigNumber(en)) + "/" + (BigNumber(men)) + " J");
    }


    public static String BigNumber(double number){
        if(number >= 1000000000){
            return String.format("%.2fB", number/ 1000000000.0);
        }

        else if(number >= 1000000){
            return String.format("%.2fM", number/ 1000000.0);
        }

        else if(number >=1000){
            return String.format("%.2fK", number/ 1000.0);
        }
        return String.valueOf(number);
    }

    public static ItemFrame SpawnItemFrame(Location loc, BlockFace face){
        if (face == BlockFace.DOWN){
            loc.setY(loc.getY() - 1);
        }
        else if (face == BlockFace.UP){
            loc.setY(loc.getY() + 1);
        }
        else if (face == BlockFace.SOUTH){
            loc.setZ(loc.getZ() + 1);
        }
        else if (face == BlockFace.NORTH){
            loc.setZ(loc.getZ() - 1);
        }
        else if (face == BlockFace.WEST){
            loc.setX(loc.getX() - 1);
        }
        else if (face == BlockFace.EAST){
            loc.setX(loc.getX() + 1);
        }
        ItemFrame i = (ItemFrame) loc.getWorld().spawn(loc, ItemFrame.class);
        i.setFacingDirection(face);
        return i;
    }

    public static Location getitemfameloc(Location loc, BlockFace face){
        if (face == BlockFace.DOWN){
            loc.setY(loc.getY() - 1);
        }
        else if (face == BlockFace.UP){
            loc.setY(loc.getY() + 1);
        }
        else if (face == BlockFace.SOUTH){
            loc.setZ(loc.getZ() + 1);
        }
        else if (face == BlockFace.NORTH){
            loc.setZ(loc.getZ() - 1);
        }
        else if (face == BlockFace.WEST){
            loc.setX(loc.getX() - 1);
        }
        else if (face == BlockFace.EAST){
            loc.setX(loc.getX() + 1);
        }
        return loc;
    }

    public static Block getTargetBlock(Player p){
        Block b = null;
        RayTraceResult ray = p.getWorld().rayTraceBlocks(p.getEyeLocation(),
                p.getEyeLocation().getDirection(),
                100d);
        if(ray != null){
            b = ray.getHitBlock();
        }
        return b;
    }

    public static ArrayList<Block> loopblockscube(Location center,int size){
        long now = System.currentTimeMillis();
        ArrayList<Block> blocks = new ArrayList<Block>();
        int X = center.getBlockX();
        int Y = center.getBlockY();
        int Z = center.getBlockZ();
        for (int x = X - size; x <= X + size; x++) {
            for (int y = Y - size; y <= size; y++) {
                for (int z = Z - size; z <= Z + size; z++) {
                    blocks.add(center.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        System.out.println("loopblockscube took " + (System.currentTimeMillis() - now));
        return blocks;
    }

    public static void giveItem(Player p, ItemStack i){
        if (p.getInventory().firstEmpty() == -1){
            p.getWorld().dropItem(p.getLocation(),i);
        }
        else {
            p.getInventory().addItem(i);
        }
    }

    public static void packetDrop(Player p,Location loc,ItemStack item){

    }

    public static void packetHideEntity(Entity e, Player p){
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        int id = e.getEntityId();
        packet.getIntegers().write(0,1);
        packet.getIntegers().write(1,id);
        for( Player on : Bukkit.getServer().getOnlinePlayers()) {
            try {
                manager.sendServerPacket(on, packet);
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
        }
    }

    @Override
    public String getPluginVersion() {
        return null;
    }
    /*
    public static void aiflyto(Entity e, Location loc, long duration) {
        long times = duration/50;
        double x1 = e.getLocation().getX();
        double x2 = loc.getX();

        double y1 = e.getLocation().getY();
        double y2 = loc.getY();

        double z1 = e.getLocation().getZ();
        double z2 = loc.getZ();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        double mx;
        double my;
        double mz;
        Location tploc;
        for (int i = 0; i < times; i++) {
            mx = x1 + dx / times * i;
            my = y1 + dy / times * i;
            mz = z1 + dz / times * i;
            tploc = new Location(e.getWorld(), mx, my, mz);
            e.teleport(tploc);
        }
         */
    // }
}
