package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.event.faction.FactionLeaveEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Parseable;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LeaveSubCommand extends SubCommand {
    public LeaveSubCommand() {
        super("leave", LangMessage.LEAVE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);

            if (!FactionUtils.getPlayerRank(faction, player).toString().equals(OwnerRank.registry)) {
                FactionLeaveEvent leaveEvent = new FactionLeaveEvent(faction, player);
                Bukkit.getPluginManager().callEvent(leaveEvent);
                if (faction.Leave(player) && !leaveEvent.isCancelled())
                    Language.sendMessage(LangMessage.LEAVE_SUCCESS, player, new Parseable("{faction_displayname}", faction.getDisplayName()));
                else if (leaveEvent.isCancelled())
                    Language.sendMessage(LangMessage.LEAVE_OWN_FACTION, player);
                else
                    CommandExecuteError(CommandExecuteError.OtherError, player);
            } else {
                player.sendMessage(Language.getPrefix() + "§cCannot leave your own faction. Delete it or give someone else owner rights");
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        if (FactionUtils.getFaction(player) == null) {
            result = false;
        }
        return result;
    }
}
