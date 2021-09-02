package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnInteract implements Listener {

    @EventHandler
    public void Interact(PlayerInteractEvent event) {
        PlayerData playerData = ImprovedFactionsMain.playerData.get(event.getPlayer().getUniqueId());
        if (event.getClickedBlock() == null) return;
        Faction claimFaction = ChunkUtils.GetFactionClaimedChunk(event.getClickedBlock().getChunk());

        if (claimFaction == null)
            return;

        if (FactionUtils.getFaction(event.getPlayer()) == null) {
            event.setCancelled(true);
            return;
        }

        if (!claimFaction.getRegistryName()
                .equals(playerData.playerFaction.getRegistryName())) {
            event.setCancelled(true);
        }
    }
}
