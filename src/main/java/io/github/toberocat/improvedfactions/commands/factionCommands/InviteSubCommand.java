package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.event.faction.FactionLeaveEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InviteSubCommand extends SubCommand {
    public InviteSubCommand() {
        super("invite", "Invite someone into your faction");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);
            if (faction.hasPermission(player, Faction.INVITE_PERMISSION)) {
                if (args.length == 1) {
                    Player playerToInvite = Bukkit.getPlayer(args[0]);

                    if (playerToInvite != null && playerToInvite.isOnline()) {
                        TextComponent textComponent = new TextComponent(Language.getPrefix() + "§e" + faction.getDisplayName() + " §finvited you");
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join" + JoinPrivateFactionSubCommand.joinUUID + " " + faction.getRegistryName()));
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join the faction").create()));
                        playerToInvite.spigot().sendMessage(textComponent);
                    } else {
                        CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
                    }
                } else {
                    CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
                }
            } else {
                CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            for (Player on : Bukkit.getOnlinePlayers())
                arguments.add(on.getName());
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        Faction faction = FactionUtils.getFaction(player);
        if (faction == null)
            result = false;
        else if (!faction.hasPermission(player, Faction.INVITE_PERMISSION))
            result = false;
        return result;
    }
}
