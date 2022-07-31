package com.neomechanical.neoconfig.menu.actions;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoutils.inventory.GUIAction;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ChangeKey extends GUIAction {
    private final String initialKeyValue;
    private final ConfigurationSection key;
    private final String subKey;
    private final File file;
    private final FileConfiguration config;

    public ChangeKey(String initialKeyValue, String subKey, FileConfiguration config, File file, ConfigurationSection key) {
        this.initialKeyValue = initialKeyValue;
        this.subKey = subKey;
        this.config = config;
        this.key = key;
        this.file = file;
    }
    @Override
    public void action(Player player) {
        if (initialKeyValue.length()>50) {
            player.sendMessage("Key value is too long. Max length is 50 characters, use manual command to change key");
            return;
        }
        new AnvilGUI.Builder()
                .onComplete((playerAuthor, text) -> {                                    //called when the inventory output slot is clicked
                    key.set(subKey, text);
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return AnvilGUI.Response.close();
                })
                .text(initialKeyValue)                              //sets the text the GUI should start with
                .title("Change key")                                       //set the title of the GUI (only works in 1.14+)
                .plugin(NeoConfig.getInstance())                                          //set the plugin instance
                .open(player);
    }
}
