package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands.DisbandFactionCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands.JoinPrivateAdminSubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminSubCommand extends SubCommand {

    public List<SubCommand> subCommands;

    public AdminSubCommand() {
        super("admin", LangMessage.ADMIN_DESCRIPTION);
        subCommands = new ArrayList<>();
        subCommands.add(new DisbandFactionCommand());
        subCommands.add(new JoinPrivateAdminSubCommand());
    }


    @Override
    protected void CommandExecute(Player player, String[] args) {
        if(!SubCommand.CallSubCommands(subCommands, player, args)) {
            Language.sendMessage(LangMessage.THIS_COMMAND_DOES_NOT_EXIST, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return SubCommand.CallSubCommandsTab(subCommands, player, args);
    }
}
