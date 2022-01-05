package extension.settingsandflags.listeneea;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import extension.settingsandflags.Main;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;

public class FireSpreadListener implements Listener {

	@EventHandler
	public void OnFireSpread(BlockIgniteEvent event) {
		if (event.getCause() == IgniteCause.FLINT_AND_STEEL) return;
		Faction faction = ChunkUtils.GetFactionClaimedChunk(event.getBlock().getChunk());
		if (faction == null) return;
		if (!faction.getSettings().getFlags().get(Main.FIRESPREAD).getCurrentBool()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnBlockBurn(BlockBurnEvent event) {
		Faction faction = ChunkUtils.GetFactionClaimedChunk(event.getBlock().getChunk());
		if (faction == null) return;
		if (!faction.getSettings().getFlags().get(Main.FIRESPREAD).getCurrentBool()) {
			event.setCancelled(true);
		}
	}
	
}
