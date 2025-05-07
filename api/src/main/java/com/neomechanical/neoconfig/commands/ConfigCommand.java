package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.api.NeoConfigAPI;
import com.neomechanical.neoconfig.menu.ConfigMenu;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class ConfigCommand extends Command {
    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Show the config file in an interactive GUI (Auto reloads config)";
    }

    @Override
    public String getSyntax() {
        return "/nc config";
    }

    @Override
    public String getPermission() {
        return "neoconfig.config";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }
    @Override
    public void perform(CommandSender player, String[] args) {
        Player playerAsPlayer = (Player) player;
        Plugin plugin = NeoConfigAPI.getProvider().getPlugin();
        ConfigMenu configMenu = new ConfigMenu(plugin);
        configMenu.onComplete((playerAsAuthor, text) -> NeoConfigAPI.getProvider().reloadConfig())
                .setPluginEditing(plugin)
                .permission("neoconfig.config",
                        () -> NeoUtils.getNeoUtilities().getManagers().getLanguageManager().getString("commandGeneric.errorNoPermission", null))
                .open(playerAsPlayer);
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
