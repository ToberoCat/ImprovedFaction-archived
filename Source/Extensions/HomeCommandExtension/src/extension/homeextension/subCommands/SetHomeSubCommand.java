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

public class SetHomeSubCommand extends SubCommand {

	public SetHomeSubCommand() {
		super("sethome", "Set the home people can teleport to");
	}

	@Override
	public SubCommandSettings getSettings() {
		return new SubCommandSettings().setFactionPermission(Main.SETHOME_PERMISSION).setNeedsFaction(NYI.Yes);
	}
	
	@Override

	protected void CommandExecute(Player player, String[] args) {
        PlayerData playerData = ImprovedFactionsMain.playerData.get(player.getUniqueId());

		Main.homes.put(playerData.playerFaction.getRegistryName(), player.getLocation());
		player.sendMessage(Language.getPrefix() + Language.format("&fSuccessfully set the home. Teleport to it suing &7/f home"));
	}

	@Override
	protected List<String> CommandTab(Player player, String[] args) {
		return null;
	}

}
