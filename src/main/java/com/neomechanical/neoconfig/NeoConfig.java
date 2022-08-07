package com.neomechanical.neoconfig;

import com.neomechanical.neoconfig.commands.RegisterCommands;
import com.neomechanical.neoutils.NeoUtils;
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

    @Override
    public void onPluginEnable() {
        setInstance(this);
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
}
