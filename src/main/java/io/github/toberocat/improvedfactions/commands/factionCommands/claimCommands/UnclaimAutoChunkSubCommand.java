package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class UnclaimAutoChunkSubCommand extends SubCommand {

    public static boolean autoUnclaim = false;

    public UnclaimAutoChunkSubCommand() {
        super("auto", "unclaim.auto", "Automatically unclaims all chunks you walk in. &a&lToggleable");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            autoUnclaim = !autoUnclaim;
            player.sendMessage(Language.getPrefix() +
                    Language.format(((autoUnclaim ? "&a&lEnabled&r " : "&c&lDisabled&r ") + "&fauto unclaim")));
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
