package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.event.faction.FactionJoinEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.gui.FlagUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfoSubCommand extends SubCommand {
    public InfoSubCommand() {
        super("info", "This will show the name and members of your faction");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length >= 1) {
            Faction faction = FactionUtils.getFactionByRegistry(args[0]);
            if (faction == null) {
                player.sendMessage(faction.getDescription());
            } else {
                player.sendMessage(Language.getPrefix() + "§cDidn't find the faction");
            }
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
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
