package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.utility.Callback;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class OnInteract implements Listener {

    public static List<Callback> callbacks = new ArrayList<>();

    @EventHandler
    public void Interact(PlayerInteractEvent event) {
        if (!ImprovedFactionsMain.getPlugin().getConfig().getBoolean("general.allowClaimProtection")) return;
        PlayerData playerData = ImprovedFactionsMain.playerData.get(event.getPlayer().getUniqueId());
        if (event.getClickedBlock() == null) return;
        Faction claimFaction = ChunkUtils.GetFactionClaimedChunk(event.getClickedBlock().getChunk());
            if (claimFaction == null)
                return;

            if (FactionUtils.getFaction(event.getPlayer()) == null) {
                return;
            }

            if (!claimFaction.getRegistryName()
                    .equals(playerData.playerFaction.getRegistryName())) {
                boolean result = Callback.GetCallbacks(callbacks, playerData.playerFaction, event.getPlayer(), event.getClickedBlock());
                event.setCancelled(result);
            }
    }
}
