package io.github.toberocat.core.commands.factions;

import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.factions.FactionUtility;
import io.github.toberocat.core.utility.factions.rank.members.MemberRank;
import io.github.toberocat.core.utility.factions.rank.Rank;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinFactionSubCommand extends SubCommand {
    public JoinFactionSubCommand() {
        super("join", "", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setArgLength(1).setNeedsFaction(SubCommandSettings.NYI.No);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = FactionUtility.getFactionByRegistry(args[0]);
        if (faction == null) {
            Language.sendRawMessage("&cCan't find given faction", player);
            return;
        }

        if (faction.getOpenType() == Faction.OpenType.CLOSED) {
            Language.sendRawMessage("&cGiven faction is private", player);
            return;
        }

        Result result = faction.join(player, Rank.fromString(MemberRank.registry));
        if (result.isSuccess()) {
            Language.sendRawMessage("Joined &e" + faction.getDisplayName(), player);
        } else {
            Language.sendRawMessage(result.getPlayerMessage(), player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return FactionUtility.getAllFactions();
    }
}
