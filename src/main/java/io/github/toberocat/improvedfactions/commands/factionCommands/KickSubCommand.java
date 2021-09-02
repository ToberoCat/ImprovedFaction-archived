package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.event.faction.FactionDeleteEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KickSubCommand extends SubCommand {
    public KickSubCommand() {
        super("kick", "Kicks a player from your faction. &6Warning: &fWith this command, you remove the player from the faction");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);
            if (FactionUtils.getPlayerRank(faction, player).isAdmin()) {
                if (args.length >= 1) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    if (faction.Leave(p)) {
                        player.sendMessage(Language.getPrefix() + "§fKicked §e" + p.getName());
                        ImprovedFactionsMain.getPlugin().getPlayerMessages().SendMessage(p, Language.getPrefix() + "§cYou were kicked from §e" + faction.getDisplayName());
                    } else {
                        CommandExecuteError(CommandExecuteError.OtherError, player);
                    }
                } else {
                    CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
                }
            } else {
                CommandExecuteError(CommandExecuteError.OnlyAdminCommand, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            PlayerData data = ImprovedFactionsMain.playerData.get(player.getUniqueId());
            for (FactionMember uuid : data.playerFaction.getMembers()) {
                if (uuid != null) {
                    arguments.add(Bukkit.getOfflinePlayer(uuid.getUuid()).getName());
                }
            }
        }
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
