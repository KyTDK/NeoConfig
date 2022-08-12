package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commandManager.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin's config file";
    }

    @Override
    public String getSyntax() {
        return "/np reload";
    }

    @Override
    public String getPermission() {
        return "neoconfig.reload";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }
    JavaPlugin plugin = NeoConfig.getInstance();
    @Override
    public void perform(CommandSender commandSender, String[] strings) {
        plugin.reloadConfig();
        NeoUtils.getLanguageManager().loadLanguageConfig();
    }

    @Override
    public List<String> tabSuggestions() {
        return null;
    }

    @Override
    public Map<String, List<String>> mapSuggestions() {
        return null;
    }
}
