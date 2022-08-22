package com.neomechanical.neoconfig.menu;

import com.neomechanical.neoconfig.menu.actions.ChangeKey;
import com.neomechanical.neoutils.config.yaml.YamlConfSection;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import com.neomechanical.neoutils.inventory.actions.OpenInventory;
import com.neomechanical.neoutils.inventory.managers.data.InventoryGUI;
import com.neomechanical.neoutils.inventory.managers.data.InventoryItem;
import com.neomechanical.neoutils.items.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.neomechanical.neoutils.config.yaml.YamlKeys.getKeys;
import static com.neomechanical.neoutils.config.yaml.YamlUtils.*;

public class ConfigMenu {
    private final Plugin plugin;
    private BiConsumer<Player, String> completeFunction;
    private String perm = null;
    private String title = "Change key";
    private Supplier<String> permMessage = () -> "<red><bold>You do not have permission to use this command";
    private Consumer<Player> closeFunction;

    public ConfigMenu(Plugin plugin) {
        this.plugin = plugin;
    }

    private InventoryGUI generateMenu(@Nullable Plugin plugin) {
        if (plugin != null) {
            //Create plugin menu with all the keys
            InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, plugin.getName());
            if (addFiles(pluginMenu, plugin.getDataFolder().listFiles())) {
                //Add pluginMenu item to main menu
                return pluginMenu;
            }
        }
        InventoryGUI menu = InventoryUtil.createInventoryGUI(null, 54, "NeoConfig");
        //get plugins
        List<Plugin> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).collect(Collectors.toList());
        for (Plugin p : plugins) {
            createPluginItem(p, menu);
        }
        return menu;
    }

    @SuppressWarnings("unused")
    public ConfigMenu onComplete(BiConsumer<Player, String> completeFunction) {
        this.completeFunction = completeFunction;
        return this;
    }

    public ConfigMenu onClose(Consumer<Player> closeFunction) {
        this.closeFunction = closeFunction;
        return this;
    }

    public ConfigMenu title(String title) {
        this.title = title;
        return this;
    }

    @SuppressWarnings("unused")
    public ConfigMenu permission(String perm, Supplier<String> permMessage) {
        this.perm = perm;
        this.permMessage = permMessage;
        return this;
    }

    public void open(Player player, @Nullable Plugin plugin) {
        InventoryGUI menu = generateMenu(plugin);
        InventoryUtil.openInventory(player, menu);
    }

    private void createPluginItem(Plugin p, InventoryGUI menu) {
        ItemStack item = ItemUtil.createItem(Material.BOOKSHELF, ChatColor.RESET + p.getName());

        //Create plugin menu with all the keys
        InventoryGUI pluginMenu = InventoryUtil.createInventoryGUI(null, 54, p.getName());
        if (addFiles(pluginMenu, p.getDataFolder().listFiles())) {
            //Add pluginMenu item to main menu
            InventoryItem inventoryItem = new InventoryItem(item, (event) -> new OpenInventory(pluginMenu).action(event), null);
            menu.addItem(inventoryItem);
        }
        pluginMenu.setOpenOnClose(menu);
    }

    //PluginMenu contains all the plugins interface items
    private boolean addFiles(InventoryGUI pluginMenu, File[] dataFolder) {
        if (dataFolder == null) {
            return false;
        }
        for (File file : dataFolder) {
            // Add all yml files in the data folder that are directories
            if (file.isDirectory()) {
                File[] dirFiles = file.listFiles();
                File[] ymlFiles = file.listFiles((dir, name) -> name.endsWith(".yml"));
                if (ymlFiles != null && ymlFiles.length == 0) {
                    continue;
                }
                InventoryGUI directory = InventoryUtil.createInventoryGUI(null, 54, file.getName());
                directory.setOpenOnClose(pluginMenu);
                addFiles(directory, dirFiles);
                pluginMenu.addItem(new InventoryItem(ItemUtil.createItem(Material.CHEST, ChatColor.RESET + file.getName()),
                        (event) -> new OpenInventory(directory).action(event), null));
                continue;
            }
            // Add all yml files inside the plugin data folder (excluding directories)
            addFile(file, pluginMenu);
        }
        return true;
    }

    private void addFile(File file, InventoryGUI pluginMenu) {
        if (file.getName().endsWith(".yml")) {
            Yaml config = new Yaml();
            YamlConfSection data;
            try {
                InputStream targetStream = new FileInputStream(file);
                Map<String, Object> dataRaw = config.load(targetStream);
                data = new YamlConfSection(file.getName(), dataRaw);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            //Create YML item with all keys
            InventoryGUI keysMenu = InventoryUtil.createInventoryGUI(null, 54, "Keys");
            keysMenu.setOpenOnClose(pluginMenu);
            //Add Key items to configYMLMenu
            if (addKeys(config, data, file, keysMenu, plugin)) {
                ItemStack item = ItemUtil.createItem(Material.BOOK, ChatColor.RESET + file.getName());
                InventoryItem ymlFile = new InventoryItem(item, (event) -> new OpenInventory(keysMenu).action(event), null);
                pluginMenu.addItem(ymlFile);
            }
            // Add YAML fields
            addSubKeys(config, file, data, keysMenu, plugin);
        }
    }

    private boolean addKeys(Yaml config, YamlConfSection configurationSection, File file,
                            InventoryGUI configYMLMenu, Plugin pluginEditing) {
        ArrayList<YamlConfSection> keys = getConfigurationSections(configurationSection.data);
        if (keys == null) {
            return false;
        }
        if (getKeys(false, configurationSection.data) == null) {
            return false;
        }
        for (YamlConfSection key : keys) {
            if (key == null || getKeys(false, key.data) == null) {
                continue;
            }
            String keyName = key.name;
            ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RESET + keyName);
            //Create GUI for all the keys
            InventoryGUI keyMenu = InventoryUtil.createInventoryGUI(null, 54, keyName);
            keyMenu.setOpenOnClose(configYMLMenu);
            if (addSubKeys(config, file, key, keyMenu, pluginEditing)) {
                InventoryItem inventoryItem = new InventoryItem(item, (event) -> new OpenInventory(keyMenu).action(event), null);
                //Add keyMenu item to configYMLMenu
                configYMLMenu.addItem(inventoryItem);
            }
        }
        return true;
    }

    private boolean addSubKeys(Yaml config, File file, YamlConfSection data, InventoryGUI keyMenu, Plugin pluginEditing) {
        Set<String> keys = getKeys(false, data.data);
        if (keys == null) {
            return false;
        }
        for (String subKey : keys) {
            YamlConfSection configSection = getConfigurationSection(data.data, subKey);
            if (isConfigurationSection(data.data, subKey) && configSection != null) {
                addKeys(config, configSection, file, keyMenu, pluginEditing);
                continue;
            }
            ItemStack item = ItemUtil.createItem(Material.TRIPWIRE_HOOK, ChatColor.RESET + subKey);
            String perm2;
            if (perm != null) {
                perm2 = perm;
            } else {
                perm2 = "neoconfig.edit." + pluginEditing.getName();

            }
            ChangeKey.ChangeKeyBuilder changeKeyBuilder = new ChangeKey.ChangeKeyBuilder(config, subKey, file, data.data, plugin);
            changeKeyBuilder.setCompleteFunction(completeFunction);
            changeKeyBuilder.setCloseFunction(closeFunction);
            changeKeyBuilder.setPerm(perm2);
            changeKeyBuilder.setTitle(title);
            changeKeyBuilder.setRestoreInventory(keyMenu);
            changeKeyBuilder.setPermMessage(permMessage);
            InventoryItem inventoryItem = new InventoryItem(item, (event) -> changeKeyBuilder.build().action(event), null);
            keyMenu.addItem(inventoryItem);
        }
        return true;
    }
}
