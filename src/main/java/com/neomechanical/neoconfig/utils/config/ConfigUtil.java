package com.neomechanical.neoconfig.utils.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ConfigUtil {
    private ConfigUtil() {}

    @NotNull
    public static ConfigurationSection[] getConfigurationSections(@NotNull ConfigurationSection section) {
        return section.getKeys(false).stream()
                .filter(section::isConfigurationSection)
                .map(section::getConfigurationSection)
                .toArray(ConfigurationSection[]::new);
    }
}
