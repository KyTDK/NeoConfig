package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoconfig.menu.ConfigMenu;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commandManager.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class MainCommand extends Command {
    @Override
    public String getName() {
        return "nc";
    }

    @Override
    public String getDescription() {
        return "Open the NeoConfig menu";
    }

    @Override
    public String getSyntax() {
        return "/nc";
    }

    @Override
    public String getPermission() {
        return "neoconfig.admin";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void perform(CommandSender player, String[] args) {
        Player playerAsPlayer = (Player) player;
        new ConfigMenu(NeoConfig.getInstance())
                .permission("neoconfig.admin",
                        () -> NeoUtils.getLanguageManager().getString("commandGeneric.errorNoPermission", null))
                .open(playerAsPlayer, null);
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
