package extension.homeextension.subCommands;

import java.util.List;

import org.bukkit.entity.Player;

import extension.homeextension.Main;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings.NYI;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.language.Language;

public class HomeSubCommand extends SubCommand {

	public HomeSubCommand() {
		super("home", "Teleports you to the faction home");
	}

	@Override
	public SubCommandSettings getSettings() {
		return new SubCommandSettings().setFactionPermission(Main.HOME_PERMISSION).setNeedsFaction(NYI.Yes);
	}
	
	@Override
	protected void CommandExecute(Player player, String[] args) {
		PlayerData playerData = ImprovedFactionsMain.playerData.get(player.getUniqueId());
		if (Main.homes.containsKey(playerData.playerFaction.getRegistryName())) {
			player.teleport(Main.homes.get(playerData.playerFaction.getRegistryName()));			
		} else { 
			player.sendMessage(Language.getPrefix() + Language.format("&cCannot find a home for your faction. Ask your faction owner to set one"));
		}
	}

	@Override
	protected List<String> CommandTab(Player player, String[] args) {
		return null;
	}

}
