package extension.settingsandflags.listeneea;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import extension.settingsandflags.Main;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class InteractListnener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void Interact(PlayerInteractEvent event) {
		Faction faction = ChunkUtils.GetFactionClaimedChunk(event.getPlayer().getLocation().getChunk());
		if (faction == null) return;
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof InventoryHolder) {
			if (!faction.hasPermission((Player)event.getPlayer(), Main.OPEN_INVENTORY_PERMISSION)) {
				event.setCancelled(true);
			}
		} else if(event.getClickedBlock().getType().toString().contains("DOOR")) {
			if (!faction.hasPermission((Player)event.getPlayer(), Main.OPEN_DOORS_PERMISSION)) {
				event.setCancelled(true);
			}
		} else if (event.getClickedBlock().getType().toString().contains("PRESSURE_PLATE")) {
			if (!faction.hasPermission((Player)event.getPlayer(), Main.USE_REDSTONE_PERMISSION)) {
				event.setCancelled(true);
			}
		}
	}

}
