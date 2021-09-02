package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class VersionSubCommand extends SubCommand {
    public VersionSubCommand() {
        super("version", "Shows the version currently installed");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        try {
            player.sendMessage(Language.getPrefix() + Language.format("&fRunning version " + ImprovedFactionsMain.updateChecker.getVersion()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
