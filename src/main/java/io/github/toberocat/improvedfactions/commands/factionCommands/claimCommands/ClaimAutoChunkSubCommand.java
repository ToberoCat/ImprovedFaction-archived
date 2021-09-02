package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimAutoChunkSubCommand extends SubCommand {

    public static boolean autoClaim;
    public ClaimAutoChunkSubCommand() {
        super("auto", "claimChunk.auto", "Automatically claims all chunks you walk in. &a&lToggleable");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            autoClaim = !autoClaim;
            player.sendMessage(Language.getPrefix() +
                    Language.format(((autoClaim ? "&a&lEnabled&r " : "&c&lDisabled&r ") + "&fauto unclaim")));
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
