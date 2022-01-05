package extension.chatmessageextension.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import extension.chatmessageextension.Main;
import extension.chatmessageextension.messages.ChatMessage;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;

public class SendMessage extends SubCommand {

	public SendMessage() {
		super("send", "Send a message to the chat");
	}

	@Override
	public SubCommandSettings getSettings() {
		return super.getSettings()
				.setAllowAliases(true)
				.setNeedsAdmin(false)
				.setFactionPermission(Main.FACTION_CHAT_SEND);
	}
	
	@Override
	protected void CommandExecute(Player player, String[] args) {
		String message = String.join(" ", args);
		
        PlayerData playerData = ImprovedFactionsMain.playerData.get(player.getUniqueId());

        Faction faction = playerData.playerFaction;
        
		for (FactionMember member : faction.getMembers()) {
			if (member != null) {
				Player online = Bukkit.getPlayer(member.getUuid());
				if(online != null && faction.hasPermission(player, Main.FACTION_CHAT_READ)) {
					online.sendMessage("§7[§e" + faction.getDisplayName() + " Chat§7] §f" + player.getDisplayName() + ": §r" + message.trim());
				}
			}
		}
		
		Main.AddChatMessage(new ChatMessage(message.trim(), player.getUniqueId()), faction);
	}

	@Override
	protected List<String> CommandTab(Player arg0, String[] arg1) {
		return null;
	}

}
