package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoutils.inventory.GUIAction;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ChangeKey extends GUIAction {
    private final Object initialKeyValue;
    private final ConfigurationSection key;
    private final String subKey;
    private final File file;
    private final FileConfiguration config;
    private final Plugin plugin;

    public ChangeKey(Object initialKeyValue, String subKey, FileConfiguration config, File file, ConfigurationSection key, Plugin plugin) {
        this.initialKeyValue = initialKeyValue;
        this.subKey = subKey;
        this.config = config;
        this.key = key;
        this.file = file;
        this.plugin = plugin;
    }

    @Override
    public void action(Player player) {
        new AnvilGUI.Builder()
                .onComplete((playerAuthor, text) -> {                                    //called when the inventory output slot is clicked
                    if (initialKeyValue instanceof String) {
                        key.set(subKey, text);
                    } else if (initialKeyValue instanceof Integer) {
                        key.set(subKey, Integer.parseInt(text));
                    } else if (initialKeyValue instanceof Double) {
                        key.set(subKey, Double.parseDouble(text));
                    } else if (initialKeyValue instanceof Boolean) {
                        key.set(subKey, Boolean.parseBoolean(text));
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + initialKeyValue.getClass().getName());
                    }
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return AnvilGUI.Response.close();
                })
                .text(initialKeyValue.toString())                              //sets the text the GUI should start with
                .title("Change key")                                       //set the title of the GUI (only works in 1.14+)
                .plugin(plugin)                                          //set the plugin instance
                .open(player);
    }
}
