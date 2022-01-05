package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.*;

public class ClaimAutoChunkSubCommand extends SubCommand {

    public static List<UUID> autoClaim = new ArrayList<>();
    public ClaimAutoChunkSubCommand() {
        super("auto", "claim.auto", LangMessage.AUTO_CLAIM_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            if (autoClaim.contains(player.getUniqueId())) {
                autoClaim.remove(player.getUniqueId());
            } else {
                autoClaim.add(player.getUniqueId());
            }

            if (autoClaim.contains(player.getUniqueId())) {
                Language.sendMessage(LangMessage.AUTO_CLAIM_ENABLED, player);
            } else {
                Language.sendMessage(LangMessage.AUTO_CLAIM_DISABLED, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
