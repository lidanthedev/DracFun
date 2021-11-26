package me.lidan.draconic.Other;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import me.lidan.draconic.Draconic;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SlimeFunItemSetup {
    public static ItemGroup category;

    public static void setup(SlimefunAddon plugin){
        NamespacedKey categoryId = new NamespacedKey(Draconic.getInstance(), "cool_category");
        ItemStack categoryItem = new ItemStack(Material.DIAMOND);
        ItemMeta meta = categoryItem.getItemMeta();
        meta.setDisplayName("&6&lDraconic");
        categoryItem.setItemMeta(meta);
        category = new ItemGroup(categoryId, categoryItem);
        // category.register(plugin);

        SlimefunItemStack itemStack = new SlimefunItemStack("WYVERN_ERROR", Material.WOODEN_SWORD, "&aPretty cool " +
                "Emerald",
                "", "&7This is awesome");
        ItemStack[] recipe = {
                new ItemStack(Material.DIAMOND),    null,                               new ItemStack(Material.DIAMOND),
                null,                               new ItemStack(Material.DIAMOND),    null,
                new ItemStack(Material.DIAMOND),    null,                               new ItemStack(Material.DIAMOND)
        };
        DraconicArmorPiece sfItem = new DraconicArmorPiece(category, itemStack, recipe,1000f,100);
        sfItem.register(plugin);
    }
}
