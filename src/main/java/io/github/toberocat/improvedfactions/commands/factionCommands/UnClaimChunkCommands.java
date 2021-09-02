package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.UnclaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.UnclaimOneChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UnClaimChunkCommands extends SubCommand {

    public static List<SubCommand> subCommands = new ArrayList<>();

    public UnClaimChunkCommands() {
        super("unclaimChunk", "Unlciam chunks. &6&l&nWarning: &r&fThis will remove the protection");
        subCommands.add(new UnclaimOneChunkSubCommand());
        subCommands.add(new UnclaimAutoChunkSubCommand());
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);
            if (faction.hasPermission(player, Faction.UNCLAIM_CHUNK_PERMISSION)) {
                if(!SubCommand.CallSubCommands(subCommands, player, args)) {
                    player.sendMessage(Language.getPrefix() + "§cThis command doesn't exist");
                }
            } else {
                CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return SubCommand.CallSubCommandsTab(subCommands, player, args);
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        Faction faction = FactionUtils.getFaction(player);
        if (faction == null)
            result = false;
        else if (!faction.hasPermission(player, Faction.UNCLAIM_CHUNK_PERMISSION))
            result = false;
        return result;
    }
}
