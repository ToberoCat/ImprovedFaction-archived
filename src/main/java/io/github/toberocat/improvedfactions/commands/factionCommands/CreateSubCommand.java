package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.event.faction.FactionCreateEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateSubCommand extends SubCommand {

    public CreateSubCommand() {
        super("create", "Create a faction");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) == null) {
            if (args.length == 1) {
                if (FactionUtils.getFactionByRegistry(ChatColor.stripColor(args[0])) == null) {
                    CreateFaction(player, args[0]);
                } else {
                    player.sendMessage(Language.getPrefix() + "§cThis faction already exists");
                }
            } else
                player.sendMessage(Language.getPrefix() + "§cYou need to name your faction");
        } else
            player.sendMessage(Language.getPrefix() + "§cYou can't create a faction, because you are already in one");
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<String>();
        if (args.length != 1) return arguments;

        if (player.hasPermission("faction.commands.create") && FactionUtils.getFaction(player) == null) {
            arguments.add("name");
            if (!player.hasPermission("faction.colors.colorInFactionName")
                && args[0].contains("&")) {
                player.sendMessage(Language.getPrefix() + "§6You §cdon't have §6enough permissions for colors in faction names");
            }
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        if (FactionUtils.getFaction(player) != null)
            result = false;
        return result;
    }

    private void CreateFaction(Player player, final String _name) {
        String name = player.hasPermission("faction.colors.colorInFactionName")
                ? Language.format(_name) : _name;
        Faction faction = new Faction(ImprovedFactionsMain.getPlugin(), name, Faction.OpenType.Public);
        FactionCreateEvent createEvent = new FactionCreateEvent(faction, player);
        Bukkit.getPluginManager().callEvent(createEvent);
        if (!createEvent.isCancelled()) {
            faction.Join(player, Rank.fromString(OwnerRank.registry));
            player.sendMessage(Language.getPrefix() + "§fYou §asuccessfully §fcreated your faction §e" + faction.getDisplayName());
        } else {
            faction.DeleteFaction();
            player.sendMessage(Language.getPrefix() + "§cCouldn't join. " + createEvent.getCancelMessage());
        }


    }
}
