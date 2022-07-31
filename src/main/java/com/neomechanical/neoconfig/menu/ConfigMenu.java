package com.neomechanical.neoconfig.menu;

import com.neomechanical.neoutils.config.ConfigUtil;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.actions.OpenInventory;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.inventory.managers.data.InventoryItem;
import com.neomechanical.neoutils.items.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;

public class ConfigMenu {
    public static InventoryGUI generateMenu() {
        //get plugins
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        InventoryGUI menu = InventoryUtil.createInventoryGUI(null, "menu", 54);
        for (Plugin plugin : plugins) {
            ItemStack item = ItemUtil.createItem(Material.REDSTONE_BLOCK, plugin.getName());

            //Create plugin menu with all the keys
            InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, plugin.getName(), 54);
            addFiles(plugin, pluginMenu);
            InventoryUtil.registerGUI(plugin.getName(), pluginMenu);

            //Add pluginMenu item to main menu
            InventoryItem inventoryItem = new InventoryItem(item, new OpenInventory(plugin.getName()));
            menu.addItem(inventoryItem);
        }
        InventoryUtil.registerGUI(menu.getKey(), menu);
        return menu;
    }
    //PluginMenu contains all the plugins interface items
    private static void addFiles(Plugin plugin, InventoryGUI pluginMenu) {
        File[] dataFolder = plugin.getDataFolder().listFiles((directory, fileName) -> fileName.endsWith(".yml"));
        if (dataFolder == null) {
            return;
        }
        for (File file : dataFolder) {
            ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
            //Create YML item with all keys
            String currentPath = file.getPath();
            InventoryGUI keysMenu = InventoryUtil.createInventoryGUI(null, currentPath, 54, "Keys");
            //Add Key items to configYMLMenu
            addKeys(config, keysMenu);
            InventoryUtil.registerGUI(currentPath, keysMenu);
            ItemStack item = ItemUtil.createItem(Material.PAPER, file.getName());
            InventoryItem ymlFile = new InventoryItem(item, new OpenInventory(currentPath));
            pluginMenu.addItem(ymlFile);
        }
    }
    private static void addKeys(ConfigurationSection config, InventoryGUI configYMLMenu) {
            ConfigurationSection[] keys = ConfigUtil.getConfigurationSections(config);
            for (ConfigurationSection key : keys) {
                if (key == null) {
                    continue;
                }
                ItemStack item = ItemUtil.createItem(Material.OAK_SIGN, key.getName());
                InventoryItem inventoryItem = new InventoryItem(item, new OpenInventory(key.getName()));
                configYMLMenu.addItem(inventoryItem);
            }
    }
}
