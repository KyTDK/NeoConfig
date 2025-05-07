package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.api.NeoConfigAPI;
import com.neomechanical.neoconfig.menu.ConfigMenu;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commands.Command;
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
        new ConfigMenu(NeoConfigAPI.getProvider().getPlugin())
                .permission(null,
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
