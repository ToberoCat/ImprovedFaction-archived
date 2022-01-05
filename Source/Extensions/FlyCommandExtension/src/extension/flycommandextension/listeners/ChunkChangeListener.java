package extension.flycommandextension.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import extension.flycommandextension.Main;
import extension.flycommandextension.commands.FlySubCommand;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.event.chunk.OnChunkLeaveEvent;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class ChunkChangeListener implements Listener {
	
	public static Map<UUID, BukkitRunnable> runnables = new HashMap<>();
	
	@EventHandler
	public void OnChunkEnterEvent(io.github.toberocat.improvedfactions.event.chunk.OnChunkEnterEvent event) {
		if (ChunkUtils.GetFactionClaimedChunk(event.getChunk()) != FactionUtils.getFaction(event.getPlayer()) && !runnables.containsKey(event.getPlayer().getUniqueId())) {	
			if (FlySubCommand.flying.contains(event.getPlayer().getUniqueId())) {
				if (Bukkit.getAllowFlight()) {					
					Language.sendMessage(Main.FLY_COMMAND_WARNING, event.getPlayer());

					BukkitRunnable runnable = new BukkitRunnable() {
						
						@Override
						public void run() {
							event.getPlayer().setAllowFlight(false);
							event.getPlayer().setFlying(false);
							Language.sendMessage(Main.FLY_COMMAND_DISABLED, event.getPlayer());
							FlySubCommand.flying.remove(event.getPlayer().getUniqueId());
						}
					};
					runnable.runTaskLater(ImprovedFactionsMain.getPlugin(), ImprovedFactionsMain.getPlugin().getExtConfigData().getConfig().getInt("flycommand.chunkLeaveFlightRemoval")*20);
					
					runnables.put(event.getPlayer().getUniqueId(), runnable);
				} else {
					Language.sendMessage(Main.FLY_COMMAND_ALLOW_FLIGHT_DISABLED, event.getPlayer());
				}
			}
		}
		if (ChunkUtils.GetFactionClaimedChunk(event.getChunk()) == FactionUtils.getFaction(event.getPlayer()) && runnables.containsKey(event.getPlayer().getUniqueId())) {
			runnables.get(event.getPlayer().getUniqueId()).cancel();
			runnables.remove(event.getPlayer().getUniqueId());
			Language.sendMessage(Main.FLY_COMMAND_DEWARNING, event.getPlayer());
		}
	}

}
