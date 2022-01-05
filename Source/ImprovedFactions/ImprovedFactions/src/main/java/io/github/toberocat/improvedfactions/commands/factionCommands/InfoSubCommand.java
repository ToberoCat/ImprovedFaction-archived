package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class InfoSubCommand extends SubCommand  {
    public InfoSubCommand() {
        super("info", LangMessage.INFO_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = FactionUtils.getFaction(player);
        player.sendMessage(Language.getPrefix()  +"&e&l" + faction.getDisplayName() + " &finfos");

        for (FactionMember member : faction.getMembers()) {
            if (member == null) continue;
            player.sendMessage(Language.format(Bukkit.getOfflinePlayer("&e"+member.getUuid()).getName() + "&7 - &8" + member.getRank().getDisplayName()));
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
