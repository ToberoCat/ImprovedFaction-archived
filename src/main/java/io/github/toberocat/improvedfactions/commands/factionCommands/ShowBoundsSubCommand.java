package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.utility.Utils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ShowBoundsSubCommand extends SubCommand {



    public ShowBoundsSubCommand() {
        super("showBounds", "Display the claimed chunks with particales");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Utils.drawLine(player.getLocation(), player.getLocation().add(10, 3, 4), 2, Particle.REDSTONE);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
