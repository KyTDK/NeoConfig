package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoconfig.menu.ConfigMenu;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        ConfigMenu configMenu = new ConfigMenu(NeoConfig.getInstance());
        configMenu.onComplete((playerAsAuthor, text) -> NeoConfig.reload())
                .permission("neoconfig.config",
                        () -> NeoUtils.getManagers().getLanguageManager().getString("commandGeneric.errorNoPermission", null))
                .open(playerAsPlayer, NeoConfig.getInstance());
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
