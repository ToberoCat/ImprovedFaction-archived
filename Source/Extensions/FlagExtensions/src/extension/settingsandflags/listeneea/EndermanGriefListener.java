package extension.settingsandflags.listeneea;

import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import extension.settingsandflags.Main;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class EndermanGriefListener implements Listener {

	@EventHandler
	public void OnGrief(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof Enderman) {
			Faction faction = ChunkUtils.GetFactionClaimedChunk(event.getBlock().getChunk());
			if (faction == null) return;
			if (!faction.getSettings().getFlags().get(Main.ENDERMAN_PICKUP).getCurrentBool()) {
				event.setCancelled(true);
			}
		}
	}
	
}
