package com.neomechanical.neoconfig.commands;

import com.neomechanical.neoconfig.NeoConfig;
import com.neomechanical.neoconfig.utils.messages.Messages;
import com.neomechanical.neoutils.commandManager.CommandManager;
import com.neomechanical.neoutils.commandManager.SubCommand;
import com.neomechanical.neoutils.messages.MessageUtil;
import com.neomechanical.neoutils.pages.Pagination;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class HelpCommand extends SubCommand {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "See the help menu";
    }

    @Override
    public String getSyntax() {
        return "/nc help";
    }

    @Override
    public String getPermission() {
        return "neoconfig.help";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public void perform(CommandSender player, String[] args) {
        MessageUtil messageUtil = new MessageUtil(NeoConfig.adventure());
        messageUtil.neoComponentMessage();
        int page = 1;
        if (args.length == 2) {
            if (Integer.getInteger(args[1]) == null) {
                messageUtil.sendMM(player, "<red><bold>Invaild syntax");
                return;
            }
            page = Integer.getInteger(args[1]);
        }
        List<SubCommand> pageList = Pagination.getPage(commandManager.getSubcommands(), page, 10);
        if (pageList == null) {
            messageUtil.sendMM(player, "<red><bold>Invaild syntax");
            return;
        }
        for (SubCommand subCommand : pageList) {
            messageUtil.addComponent("  <gray><bold>" + subCommand.getSyntax() + "</bold> - " + subCommand.getDescription());
        }
        messageUtil.sendNeoComponentMessage(player, Messages.MAIN_PREFIX, Messages.MAIN_SUFFIX);
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
