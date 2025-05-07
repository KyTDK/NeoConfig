package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.api.NeoConfigAPI;
import com.neomechanical.neoutils.NeoUtils;
import com.neomechanical.neoutils.commands.Command;
import com.neomechanical.neoutils.messages.MessageUtil;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class ReloadCommand extends Command {
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

    @Override
    public void perform(CommandSender commandSender, String[] strings) {
        NeoConfigAPI.getProvider().reloadConfig();
        MessageUtil.sendMM(commandSender, NeoUtils.getNeoUtilities().getManagers().getLanguageManager().getString("reload.onReload", null));
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
