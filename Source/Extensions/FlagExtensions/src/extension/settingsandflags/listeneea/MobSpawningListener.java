package extension.settingsandflags.listeneea;

import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import extension.settingsandflags.Main;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class MobSpawningListener implements Listener {
	
	@EventHandler
	public void Spawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof Monster) {
			Faction faction = ChunkUtils.GetFactionClaimedChunk(event.getEntity().getLocation().getChunk());
			if (faction == null) return;
			if (!faction.getSettings().getFlags().get(Main.HOSTILE_MOB_SPAWNING).getCurrentBool()) {
				event.setCancelled(true);
			}
		}
	}

}
