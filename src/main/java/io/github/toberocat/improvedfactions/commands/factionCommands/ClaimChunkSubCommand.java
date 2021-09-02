package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.ClaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.ClaimOneChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClaimChunkSubCommand extends SubCommand {

    public static List<SubCommand> subCommands = new ArrayList<>();

    public ClaimChunkSubCommand() {
        super("claimChunk", "Claim a chunk for your faction. This will protect it");
        subCommands.add(new ClaimOneChunkSubCommand());
        subCommands.add(new ClaimAutoChunkSubCommand());
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);
            if (faction.hasPermission(player, Faction.CLAIM_CHUNK_PERMISSION)) {
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
        else if (!faction.hasPermission(player, Faction.CLAIM_CHUNK_PERMISSION))
            result = false;
        return result;
    }
}
