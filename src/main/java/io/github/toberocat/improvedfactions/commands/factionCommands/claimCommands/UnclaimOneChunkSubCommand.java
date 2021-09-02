package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class UnclaimOneChunkSubCommand extends SubCommand {
    public UnclaimOneChunkSubCommand() {
        super("one", "unclaim.one", "Unclaims single chunk");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {

            Chunk chunk = player.getLocation().getChunk();

            Faction faction = ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction;
            if (faction.UnClaimChunk(chunk))
                player.sendMessage(Language.getPrefix() + "§aSuccessfully §funclaimed this chunk");
            else
                player.sendMessage(Language.getPrefix() + "§cCan't unclaim a chunk, that's not your property");
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
