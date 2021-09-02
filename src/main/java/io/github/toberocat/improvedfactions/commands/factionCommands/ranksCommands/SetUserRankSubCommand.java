package io.github.toberocat.improvedfactions.commands.factionCommands.ranksCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetUserRankSubCommand extends SubCommand {

    public SetUserRankSubCommand() {
        super("set", "rank.set", "Set the rank for a user");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        PlayerData data = ImprovedFactionsMain.playerData.get(player.getUniqueId());
        if (args.length == 2) {
            if (Bukkit.getPlayer(args[0]) != null) {
                if (Rank.fromString(args[1]) != null) {
                    Player p = Bukkit.getPlayer(args[0]);
                    data.playerFaction.SetRank(p, Rank.fromString(args[1]));
                    player.sendMessage(Language.getPrefix() + Language.format("&aSuccessfully &fchanged the rank for &e"
                                    + p.getDisplayName() + " &fto " + Rank.fromString(args[1])));
                } else {
                    player.sendMessage(Language.getPrefix() + "§cCannot find rank");
                }
            } else {
                CommandExecuteError(CommandExecuteError.PlayerNotFound,player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs,player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<String>();
        if (args.length == 1) {
            PlayerData data = ImprovedFactionsMain.playerData.get(player.getUniqueId());
            for (FactionMember uuid : data.playerFaction.getMembers()) {
                if (uuid != null) {
                    arguments.add(Bukkit.getPlayer(uuid.getUuid()).getName());
                }
            }
        } else if (args.length == 2) {
            System.out.println("rank");
            for (Rank rank : Rank.ranks) {
                arguments.add(rank.toString());
            }
        }
        return arguments;
    }
}
