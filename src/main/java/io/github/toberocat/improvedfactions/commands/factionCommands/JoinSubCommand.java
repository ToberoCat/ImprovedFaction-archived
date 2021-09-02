package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.event.faction.FactionJoinEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.gui.FlagUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinSubCommand extends SubCommand {
    public JoinSubCommand() {
        super("join", "Join a faction");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) == null) {
            if (args.length != 1) {
                CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
                return;
            }
            Faction faction = FactionUtils.getFactionByRegistry(args[0]);
            if (faction == null)
            {
                player.sendMessage(Language.getPrefix() + "§cCouldn't find the faction you were searching for. Maybe a spelling error");
                return;
            }

            if (FlagUtils.CompareEnum(faction.getFlag(Faction.OPENTYPE_FLAG), Faction.OpenType.Public)) {
                player.sendMessage(Language.getPrefix() + "§cCannot join this faction. The faction is private / personal");
                return;
            }

            FactionJoinEvent joinEvent = new FactionJoinEvent(faction, player);
            Bukkit.getPluginManager().callEvent(joinEvent);
            if (faction.Join(player, Rank.fromString(MemberRank.registry)) && !joinEvent.isCancelled()) {
                player.sendMessage(Language.getPrefix() + "§fYou §asuccessfully §fjoined " + faction.getDisplayName());
            } else {
                if (faction.hasMaxMembers())
                    player.sendMessage(Language.getPrefix() + "§cCouldn't join. The faction has no more space for new members");
                else if (joinEvent.isCancelled())
                    player.sendMessage(Language.getPrefix() + "§cCouldn't join. " + joinEvent.getCancelMessage());
                else
                    CommandExecuteError(CommandExecuteError.OtherError, player);
            }
        } else {
            player.sendMessage(Language.getPrefix() + "§cYou have already joined a faction. Please leave before joining another faction");
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length != 1) return arguments;
        for (Faction faction : Faction.getFactions()) {
            if (FlagUtils.CompareEnum(faction.getFlag(Faction.OPENTYPE_FLAG), Faction.OpenType.Public)) {
                arguments.add(ChatColor.stripColor(faction.getDisplayName()));
            }
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        if (FactionUtils.getFaction(player) != null)
            result = false;
        return result;
    }
}