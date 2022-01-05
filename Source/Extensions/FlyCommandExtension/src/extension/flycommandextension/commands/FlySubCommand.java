package extension.flycommandextension.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import extension.flycommandextension.Main;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings.NYI;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class FlySubCommand extends SubCommand {

	public static List<UUID> flying = new ArrayList<>();
	
	public FlySubCommand() {
		super("fly", Main.FLY_COMMAND_DESCRIPTION);
	}
	
	@Override
	public SubCommandSettings getSettings() {
		return super.getSettings().setNeedsFaction(NYI.Yes).setAllowAliases(true).setFactionPermission(Main.ALLOW_FLIGHT);
	}

	@Override
	protected void CommandExecute(Player player, String[] args) {
		if (flying.contains(player.getUniqueId())) {
			if (Bukkit.getAllowFlight()) {
				flying.remove(player.getUniqueId());
				player.setAllowFlight(false);
				player.setFlying(false);
				Language.sendMessage(Main.FLY_COMMAND_DISABLED, player);
			} else {
				Language.sendMessage(Main.FLY_COMMAND_ALLOW_FLIGHT_DISABLED, player);
			}
		} else if (ChunkUtils.GetFactionClaimedChunk(player.getLocation().getChunk()) == FactionUtils.getFaction(player)) {
			if (Bukkit.getAllowFlight()) {
				flying.add(player.getUniqueId());
				player.setAllowFlight(true);
				player.setFlying(true);
				Language.sendMessage(Main.FLY_COMMAND_ENABLED, player);
			} else {
				Language.sendMessage(Main.FLY_COMMAND_ALLOW_FLIGHT_DISABLED, player);
			}
		} else {
			Language.sendMessage(Main.FLY_COMMAND_NOT_IN_CHUNK, player);
		}
	}

	@Override
	protected List<String> CommandTab(Player arg0, String[] arg1) {
		return null;
	}

}
