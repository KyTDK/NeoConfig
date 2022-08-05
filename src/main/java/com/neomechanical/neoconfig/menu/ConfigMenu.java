package com.neomechanical.neoconfig.menu;

import com.neomechanical.neoconfig.menu.actions.ChangeKey;
import com.neomechanical.neoutils.config.ConfigUtil;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.actions.OpenInventory;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.inventory.managers.data.InventoryItem;
import com.neomechanical.neoutils.items.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ConfigMenu {
    public static InventoryGUI generateMenu() {
        //get plugins
        List<Plugin> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).toList();
        InventoryGUI menu = InventoryUtil.createInventoryGUI(null, 54, "NeoConfig");
        for (Plugin plugin : plugins) {
            ItemStack item = ItemUtil.createItem(Material.BOOKSHELF, ChatColor.RESET+plugin.getName());

            //Create plugin menu with all the keys
            InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, plugin.getName());
            if(addFiles(plugin, pluginMenu)) {
                //Add pluginMenu item to main menu
                InventoryItem inventoryItem = new InventoryItem(item, new OpenInventory(pluginMenu), null);
                menu.addItem(inventoryItem);
            }
            InventoryUtil.registerGUI(pluginMenu);
        }
        InventoryUtil.registerGUI(menu);
        return menu;
    }
    //PluginMenu contains all the plugins interface items
    private static boolean addFiles(Plugin plugin, InventoryGUI pluginMenu) {
        File[] dataFolder = plugin.getDataFolder().listFiles((directory, fileName) -> fileName.endsWith(".yml"));
        if (dataFolder == null) {
            return false;
        }
        for (File file : dataFolder) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            //Create YML item with all keys
            InventoryGUI keysMenu = InventoryUtil.createInventoryGUI(null, 54, "Keys");
            //Add Key items to configYMLMenu
            addKeys(config, file, keysMenu);
            InventoryUtil.registerGUI(keysMenu);
            ItemStack item = ItemUtil.createItem(Material.BOOK, ChatColor.RESET+file.getName());
            InventoryItem ymlFile = new InventoryItem(item, new OpenInventory(keysMenu), null);
            pluginMenu.addItem(ymlFile);
        }
        return true;
    }
    private static void addKeys(FileConfiguration config, File file, InventoryGUI configYMLMenu) {
            ConfigurationSection[] keys = ConfigUtil.getConfigurationSections(config);
            for (ConfigurationSection key : keys) {
                if (key == null) {
                    continue;
                }
                ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RESET+key.getName());
                //Create GUI for all the keys
                InventoryGUI keyMenu = InventoryUtil.createInventoryGUI(null,54, key.getName());
                addSubKeys(config, file, key, keyMenu);
                InventoryUtil.registerGUI(keyMenu);
                InventoryItem inventoryItem = new InventoryItem(item, new OpenInventory(keyMenu), null);
                //Add keyMenu item to configYMLMenu
                configYMLMenu.addItem(inventoryItem);
            }
    }
    private static void addSubKeys(FileConfiguration config, File file, ConfigurationSection key, InventoryGUI keyMenu) {
        for (String subKey : key.getKeys(false)) {
            if (subKey == null) {
                continue;
            }
            ItemStack item = ItemUtil.createItem(Material.TRIPWIRE_HOOK, ChatColor.RESET+subKey);
            InventoryItem inventoryItem = new InventoryItem(item, new ChangeKey(key.get(subKey), subKey, config, file, key), null);
            keyMenu.addItem(inventoryItem);
        }
    }
}
