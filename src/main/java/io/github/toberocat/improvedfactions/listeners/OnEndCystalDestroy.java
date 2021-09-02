package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class OnEndCystalDestroy implements Listener {

    @EventHandler
    public void OnExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal) {
            Faction claimFaction = ChunkUtils.GetFactionClaimedChunk(event.getEntity().getLocation().getChunk());

            if (claimFaction == null)
                return;

            if (!claimFaction.getFlag(Faction.ALLOW_CRYSTAL_BREAK_FLAG).getCurrentBool())
                event.setCancelled(true);
        }
    }
}
