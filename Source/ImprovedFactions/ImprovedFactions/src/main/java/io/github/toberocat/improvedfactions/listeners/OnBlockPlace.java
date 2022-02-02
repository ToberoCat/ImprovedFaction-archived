package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class OnBlockPlace implements Listener {

    @EventHandler
    public void OnPlace(BlockPlaceEvent event) {
        if (!ImprovedFactionsMain.getPlugin().getConfig().getBoolean("general.allowClaimProtection")) return;
        PlayerData playerData = ImprovedFactionsMain.playerData.get(event.getPlayer().getUniqueId());

        Faction claimFaction = ChunkUtils.GetFactionClaimedChunk(event.getBlock().getChunk());
            if (claimFaction == null)
                return;

            if (playerData.playerFaction != null && !claimFaction.getRegistryName()
                    .equals(playerData.playerFaction.getRegistryName())) {
                event.setCancelled(true);
            } else if (!claimFaction.hasPermission(event.getPlayer(), Faction.BUILD_PERMISSION)) {
                event.setCancelled(true);
            }
    }
}
