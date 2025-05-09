package com.neomechanical.neoconfig;

import com.neomechanical.neoconfig.api.NeoConfigProvider;
import com.neomechanical.neoconfig.commands.RegisterCommands;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.config.ConfigManager;
import com.neomechanical.neoutils.languages.LanguageManager;
import com.neomechanical.neoutils.manager.ManagerHandler;
import com.neomechanical.neoutils.updates.UpdateChecker;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.neomechanical.neoutils.updates.IsUpToDate.isUpToDate;

public final class NeoConfig extends JavaPlugin implements NeoConfigProvider {
    private static ManagerHandler managers;

    private Metrics metrics;


    public static void reload() {
        ConfigManager.reloadAllConfigs();
        managers.getLanguageManager().loadLanguageConfig();
    }

    public void setupBStats() {
        int pluginId = 16032;
        metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Returns an instance of the bStats Metrics object
     *
     * @return bStats Metrics object
     */
    @SuppressWarnings("unused")
    public Metrics getMetrics() {
        return metrics;
    }

    private void setLanguageManager() {
        //Set language manager before majority as they depend on its messages.
        new LanguageManager(this)
                .setLanguageCode(() -> managers.getConfigManager("config.yml").getConfig().getString("visual.language"))
                .setLanguageFile("de-DE.yml", "en-US.yml", "es-ES.yml", "fr-FR.yml", "ru-RU.yml", "tr-TR.yml", "zh-CN.yml")
                .set();
    }

    @Override
    public void onEnable() {
        managers = NeoUtils.getNeoUtilities().getManagers();
        // Create config
        managers.createNewConfigManager("config.yml");
        // Create language manager
        setLanguageManager();
        new RegisterCommands(this).register();
        setupBStats();
        new UpdateChecker(this, 104089).getVersion(version -> {
            if (!isUpToDate(this.getDescription().getVersion(), version)) {
                getLogger().info("NeoConfig v" + version + " is out. Download it at: https://www.spigotmc.org/resources/neoconfig.104089/");
            }
        });
    }

    @Override
    public void reloadConfig() {
        NeoConfig.reload();
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return NeoUtils.getNeoUtilities().getManagers().getLanguageManager();
    }
}
