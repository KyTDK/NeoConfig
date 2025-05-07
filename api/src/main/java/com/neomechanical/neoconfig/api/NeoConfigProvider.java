package com.neomechanical.neoconfig.api;

import com.neomechanical.neoutils.languages.LanguageManager;
import org.bukkit.plugin.Plugin;

public interface NeoConfigProvider {
    Plugin getPlugin();
    void reloadConfig();
    LanguageManager getLanguageManager();
}