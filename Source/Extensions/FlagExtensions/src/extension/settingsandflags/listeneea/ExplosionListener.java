package extension.settingsandflags.listeneea;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import extension.settingsandflags.Main;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class ExplosionListener implements Listener {
	
	@EventHandler
	public void OnExplosion(ExplosionPrimeEvent event) {
		Faction faction = ChunkUtils.GetFactionClaimedChunk(event.getEntity().getLocation().getChunk());
		if (faction == null) return;
		if (!faction.getSettings().getFlags().get(Main.EXPLOSIONS).getCurrentBool()) {
			event.setCancelled(true);
		}
	}

}
