package io.github.toberocat.core.commands.admin;

import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminDisbandSubCommand extends SubCommand  {
    public AdminDisbandSubCommand() {
        super("disband", LangMessage.COMMAND_ADMIN_DISBAND_DESCRIPTION, false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setArgLength(1);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = Faction.getFactionByRegistry(args[0]);
        Faction.Delete(faction);
        Language.sendMessage(LangMessage.COMMAND_ADMIN_DISBAND_SUCCESS, player);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return Faction.getAllFactions();
    }
}
