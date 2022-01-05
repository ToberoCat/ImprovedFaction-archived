package extension.chatmessageextension.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import extension.chatmessageextension.Main;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;

public class Mute extends SubCommand {

	public static Map<UUID, Boolean> mutedPlayers = new HashMap<>();
	
	public Mute() {
		super("mute", "Mute the faction chat. &a&lToggleable");
	}

	@Override
	protected void CommandExecute(Player player, String[] args) {
		
		boolean value = true;
		
		if (mutedPlayers.containsKey(player.getUniqueId())) {
			mutedPlayers.replace(player.getUniqueId(), !mutedPlayers.get(player.getUniqueId()));
			value = mutedPlayers.get(player.getUniqueId());
		} else {
			mutedPlayers.put(player.getUniqueId(), value);
		}
		
		if (value) {
			Language.sendMessage(Main.MUTED_CHAT_ENABLED, player);
		} else {
			Language.sendMessage(Main.MUTED_CHAT_DISABLED, player);
		}
	}

	@Override
	protected List<String> CommandTab(Player player, String[] args) {
		return null;
	}

}
