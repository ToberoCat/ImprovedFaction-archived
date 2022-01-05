package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanSubCommand extends SubCommand {
    public BanSubCommand() {
        super("ban", LangMessage.BANNED_PLAYER_COMMAND_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return new SubCommandSettings().setNeedsAdmin(true).setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = FactionUtils.getFaction(player);
        if (args.length == 1) {
            Player banned = Bukkit.getPlayer(args[0]);
            if (banned == null) {
                CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
                return;
            }
            UUID uuid = player.getUniqueId();
            if (!faction.getBannedPeople().contains(uuid)) {
                faction.getBannedPeople().add(uuid);
                Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_SUCCESS, player,
                        new Parseable("{banned}", args[0]));
                faction.Leave(banned);

                Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_LEAVE, banned,
                        new Parseable("{faction_displayName}", faction.getDisplayName()));
            } else {
                Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_ALREADY, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            PlayerData data = ImprovedFactionsMain.playerData.get(player.getUniqueId());
            for (Player on : Bukkit.getOnlinePlayers()) {
                if (on != null && data.playerFaction.getBannedPeople().contains(on.getUniqueId())) {
                    OfflinePlayer ofPlayer = Bukkit.getPlayer(on.getUniqueId());
                    if (ofPlayer == null) continue;
                    arguments.add(ofPlayer.getName());
                }
            }
        }
        return arguments;
    }
}
