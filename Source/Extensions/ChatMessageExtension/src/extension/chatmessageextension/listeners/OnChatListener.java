package extension.chatmessageextension.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;

public class OnChatListener implements Listener {

	@EventHandler
	public void OnChat(AsyncPlayerChatEvent event) {
		System.out.println("Called event");
		Faction faction = FactionUtils.getFaction(event.getPlayer());
        if (faction != null)
            event.setFormat("§7[§e" + faction.getDisplayName() + "§7] §r"
                    + event.getPlayer().getDisplayName() + ": " + event.getMessage());
        else
            event.setFormat(event.getPlayer().getDisplayName() + ": " + event.getMessage());
	}
	
}
