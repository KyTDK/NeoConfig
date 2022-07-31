package com.neomechanical.neoconfig;

import com.neomechanical.neoconfig.commands.RegisterCommands;
import com.neomechanical.neoutils.inventory.InventoryUtil;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class NeoConfig extends JavaPlugin {
    private static BukkitAudiences adventure;
    private static NeoConfig instance;
    private void setInstance(NeoConfig instance) {
        NeoConfig.instance = instance;
    }
    public static NeoConfig getInstance() {
        return instance;
    }
    public static @NonNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
    @Override
    public void onEnable() {
        setInstance(this);
        // Plugin startup logic
        adventure = BukkitAudiences.create(this);
        InventoryUtil.init(NeoConfig.getInstance());
        RegisterCommands.register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
