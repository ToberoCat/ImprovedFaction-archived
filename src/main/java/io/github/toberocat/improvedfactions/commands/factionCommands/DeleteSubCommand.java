package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.event.faction.FactionDeleteEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteSubCommand extends SubCommand {
    public DeleteSubCommand() {
        super("delete", "Delete your faction. Be aware. This will remove it");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);

            if (FactionUtils.getPlayerRank(faction, player).isAdmin()) {
                FactionDeleteEvent deleteEvent = new FactionDeleteEvent(faction, player);
                Bukkit.getPluginManager().callEvent(deleteEvent);
                if (faction.DeleteFaction() && !deleteEvent.isCancelled()) {
                    player.sendMessage(Language.getPrefix() + "§fYou §asuccessfully §fdeleted " + faction.getDisplayName());
                } else {
                    if (deleteEvent.isCancelled())
                        player.sendMessage(Language.getPrefix() + "§cCouldn't delte this faction. " + deleteEvent.getCancelMessage());
                    else
                        CommandExecuteError(CommandExecuteError.OtherError, player);
                }
            } else {
                CommandExecuteError(CommandExecuteError.OnlyAdminCommand, player);
            }
        } else {
            player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction to delete it");
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
        Faction faction = FactionUtils.getFaction(player);
        if (faction == null) {
            result = false;
        }
        if (faction != null && FactionUtils.getPlayerRank(faction, player) != null && !FactionUtils.getPlayerRank(faction, player).isAdmin())
            result = false;
        return result;
    }
}
