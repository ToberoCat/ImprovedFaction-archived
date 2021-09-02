package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimOneChunkSubCommand extends SubCommand {
    public ClaimOneChunkSubCommand() {
        super("one", "claimChunk.one", "Claim single chunk");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {

            Chunk chunk = player.getLocation().getChunk();

            Faction faction = ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction;
            if (faction.ClaimChunk(chunk))
                player.sendMessage(Language.getPrefix() + "§aSuccessfully §fclaimed this chunk");
            else if (faction.getPower() <= faction.getClaimedchunks()) {
                player.sendMessage(Language.getPrefix() + "§cCan't claim anymore. Your faction's claim power is 0");
            } else {
                player.sendMessage(Language.getPrefix() + "§cCan't claim a chunk owned by another faction");
            }
        } else {
            player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction to delete it");
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
