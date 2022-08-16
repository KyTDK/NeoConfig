package com.neomechanical.neoconfig;

import com.neomechanical.neoconfig.commands.RegisterCommands;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.config.ConfigManager;
import com.neomechanical.neoutils.languages.LanguageManager;
import com.neomechanical.neoutils.updates.UpdateChecker;
import org.bstats.bukkit.Metrics;

import static com.neomechanical.neoutils.updates.IsUpToDate.isUpToDate;

public final class NeoConfig extends NeoUtils {
    private static NeoConfig instance;

    private void setInstance(NeoConfig instance) {
        NeoConfig.instance = instance;
    }

    public static NeoConfig getInstance() {
        return instance;
    }

    private Metrics metrics;

    public static void reload() {
        ConfigManager.reloadAllConfigs();
        NeoUtils.getLanguageManager().loadLanguageConfig();
    }

    @Override
    public void onPluginEnable() {
        setInstance(this);
        // Create config
        new ConfigManager("config.yml");
        // Create language manager
        setLanguageManager();
        RegisterCommands.register();
        setupBStats();
        new UpdateChecker(this, 104089).getVersion(version -> {
            if (!isUpToDate(this.getDescription().getVersion(), version)) {
                getLogger().info("NeoConfig v" + version + " is out. Download it at: https://www.spigotmc.org/resources/neoconfig.104089/");
            }
        });
    }

    public void setupBStats() {
        int pluginId = 16032;
        metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onPluginDisable() {
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
                .setLanguageCode(() -> NeoUtils.getConfigManager("config.yml").getConfig().getString("visual.language"))
                .setLanguageFile("de-DE.yml", "en-US.yml", "es-ES.yml", "fr-FR.yml", "ru-RU.yml", "tr-TR.yml", "zh-CN.yml")
                .set();
    }
}
