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
        player.sendMessage(Language.getPrefix()  +"§e§l" + faction.getDisplayName()
                + " §finfo. Current power: §b" +
                faction.getPowerManager().getPower() + " / " + faction.getPowerManager().getMaxPower());

        player.sendMessage(Language.getPrefix()+"§eAllies: §7" + faction.getRelationManager().getAllies().size());

        for (String ally : faction.getRelationManager().getAllies()) {
            player.sendMessage(Language.getPrefix() + Language.format("&f"+ ally + "&7 - "
                    + FactionUtils.getFactionByRegistry(ally).getDisplayName()));
        }

        player.sendMessage(Language.getPrefix()+"§eWars: §7" + faction.getRelationManager().getEnemies().size());

        for (String enemy : faction.getRelationManager().getEnemies()) {
            player.sendMessage(Language.getPrefix() + Language.format("&f"+ enemy + "&7 - "
                    + FactionUtils.getFactionByRegistry(enemy).getDisplayName()));
        }

        player.sendMessage(Language.getPrefix()+"§eInvites: §7" + faction.getRelationManager().getInvites().size());

        for (String invite : faction.getRelationManager().getInvites()) {
            player.sendMessage(Language.getPrefix() + Language.format("&f"+ invite + "&7 - "
                    + FactionUtils.getFactionByRegistry(invite).getDisplayName()));
        }

        player.sendMessage(Language.getPrefix()+"Members: ");
        for (FactionMember member : faction.getMembers()) {
            if (member == null) continue;
            player.sendMessage(Language.getPrefix() + Language.format("&e"+Bukkit.getOfflinePlayer(member.getUuid()).getName() + "&7 - &8" + member.getRank().getDisplayName()));
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
