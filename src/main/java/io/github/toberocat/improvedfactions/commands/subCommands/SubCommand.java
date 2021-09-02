package io.github.toberocat.improvedfactions.commands.subCommands;

import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ClickActions;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {

    private static SubCommand lastSubCommand;

    protected enum CommandExecuteError { NoPermission, NoFaction, NotEnoughArgs, OtherError, PlayerNotFound, OnlyAdminCommand, NoFactionPermission };

    protected final String subCommand;
    protected final String permission;
    protected final String shortDescription;

//    protected boolean alreadyDisplayed = false;

    protected abstract void CommandExecute(Player player, String[] args);
    protected abstract List<String> CommandTab(Player player, String[] args);


    public SubCommand(String subCommand, String permission, String shortDescription) {
        this.subCommand = subCommand;
        this.permission = permission;
        this.shortDescription = shortDescription;
    }

    public SubCommand(String subCommand, String shortDescription) {
        this.subCommand = subCommand;
        this.permission = subCommand;
        this.shortDescription = shortDescription;
    }

    protected String getExtendedDescription() {
        return "extended description";
    }

    protected String getUsage() {
        return "usage";
    }

    public static List<String> CallSubCommandsTab(List<SubCommand> subCommands, Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) { //Means: The first subcommand is determend
            for (SubCommand command : subCommands) {
                String[] newArguments = Arrays.copyOfRange(args, 1, args.length);
                if (command.CommandDisplayCondition(player, newArguments))
                    arguments.add(command.getSubCommand());
            }
        } else {
            for (SubCommand command : subCommands) {
                if (args[0].equalsIgnoreCase(command.getSubCommand())) {
                    String[] newArguments = Arrays.copyOfRange(args, 1, args.length);
                    List<String> str = command.CommandTab(player, newArguments);
                    if (str != null)
                        arguments.addAll(str);
                }
            }
        }

        List<String> results = new ArrayList<String>();
        for (String arg : args) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(arg.toLowerCase())) {
                    results.add(a);
                }
            }
        }

        if (results.size() == 1) {
            for (SubCommand command : subCommands) {
                if  (results.contains(command.getSubCommand())) {
                    if (lastSubCommand != command) {
                        player.sendMessage(Language.getPrefix()+Language.format("&f"+command.shortDescription));
                        lastSubCommand = command;
                    }
                    break;
                }
            }
        }
        if (arguments.isEmpty())
            return null;
        return arguments;
    }

    public static boolean CallSubCommands(List<SubCommand> subCommands, Player player, String[] args) {
        if (args.length == 0) return false;
        for (SubCommand command : subCommands) {
            if (args[0].equalsIgnoreCase(command.getSubCommand())) {
                String[] newArguments = Arrays.copyOfRange(args, 1, args.length);
                command.CallSubCommand(player, newArguments);
                return true;
            }
        }
        return false;
    }

    public void CallSubCommand(Player player, String[] args) {
        if (player.hasPermission("faction.commands."+permission))
            CommandExecute(player, args);
        else
            CommandExecuteError(CommandExecuteError.NoPermission, player);
    }

    // * Callbacks
    protected void CommandExecuteError(CommandExecuteError error, Player player) {
        switch (error) {
            case NoPermission:
                player.sendMessage(Language.getPrefix() + "§cYou don't have enough permissions to use this command. Permission: faction.commands." + permission);
                break;
            case NoFaction:
                player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction to use this command");
                break;
            case NotEnoughArgs:
                player.sendMessage(Language.getPrefix() + "§cThis command needs more arguments. Please check the usage if you don't know what arguments");
                break;
            case OtherError:
                player.sendMessage(Language.getPrefix() + "§cAn error occurred while running the "+subCommand+" command");
                break;
            case PlayerNotFound:
                player.sendMessage(Language.getPrefix() + "§cCoudn't find player");
                break;
            case OnlyAdminCommand:
                player.sendMessage(Language.getPrefix() + "§cYou need admin rights to execute this command");
                break;
            case NoFactionPermission:
                player.sendMessage(Language.getPrefix() + "§cYou don't have enought permissions to use this command. If you think you should be allowed, ask a faction admin");
                break;
        }
    }
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        return player.hasPermission("faction.commands." + permission);
    }
    //? Getters and Setters
    public String getSubCommand() {
        return subCommand;
    }

    public String getPermission() {
        return permission;
    }
}
