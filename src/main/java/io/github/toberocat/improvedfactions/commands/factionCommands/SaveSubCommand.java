package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveSubCommand extends SubCommand {
    public SaveSubCommand() {
        super("save", "save", "Save the faction data to your world");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction.SaveFactions(ImprovedFactionsMain.getPlugin());
        player.sendMessage(Language.getPrefix() + "§fSaved faction data");
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
