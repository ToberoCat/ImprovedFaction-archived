package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.ClaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.UnclaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.event.chunk.OnChunkEnterEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class OnChunkEntered implements Listener {


    @EventHandler
    public void ChunkEnter(OnChunkEnterEvent event) {
        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(ImprovedFactionsMain.getPlugin(),
                "faction-claimed");
        PlayerData playerData = ImprovedFactionsMain.playerData.get(event.getPlayer().getUniqueId());

        if (UnclaimAutoChunkSubCommand.autoUnclaim && playerData.playerFaction != null) {
            if (playerData.playerFaction.UnClaimChunk(event.getChunk()))
                event.getPlayer().sendMessage(Language.getPrefix() + "§aSuccessfully §funclaimed this chunk");
            else
                event.getPlayer().sendMessage(Language.getPrefix() + "§cCan't unclaim a chunk, that's not your property");
        }

        if (ClaimAutoChunkSubCommand.autoClaim && playerData.playerFaction != null) {
            if (playerData.playerFaction.ClaimChunk(event.getChunk()))
                event.getPlayer().sendMessage(Language.getPrefix() + "§aSuccessfully §fclaimed this chunk");
            else if (playerData.playerFaction.getPower() <= playerData.playerFaction.getClaimedchunks()) {
                event.getPlayer().sendMessage(Language.getPrefix() + "§cCan't claim anymore. Your faction's claim power is 0");
            } else {
                event.getPlayer().sendMessage(Language.getPrefix() + "§cCan't claim a chunk owned by another faction");
            }
        }

        boolean oldClaimedchunk = playerData.chunkData.isInClaimedChunk;
        String oldFactionName = playerData.chunkData.factionRegistry;
        //Check if is in wildness

        playerData.chunkData.isInClaimedChunk = container.has(key, PersistentDataType.STRING);
        playerData.chunkData.factionRegistry = container.has(key, PersistentDataType.STRING) ?
                container.get(key, PersistentDataType.STRING) : playerData.chunkData.factionRegistry;
        if (playerData.chunkData.isInClaimedChunk != oldClaimedchunk) {
            playerData.display.alreadyDisplayedRegion = false;
        }
        if (!playerData.chunkData.factionRegistry.equals(oldFactionName)) {
            playerData.display.alreadyDisplayedRegion = false;
        }
        if (!playerData.chunkData.isInClaimedChunk) {
            if (!playerData.display.alreadyDisplayedRegion) {
                event.getPlayer().sendTitle("§2Wildness", "", 10, 20, 10);
                playerData.display.alreadyDisplayedRegion = true;
            }
        }else {
            if (!playerData.display.alreadyDisplayedRegion) {
                String factionRegistry = container.get(key, PersistentDataType.STRING);
                Faction faction = FactionUtils.getFactionByRegistry(factionRegistry);

                if (faction == null) {
                    container.remove(ChunkUtils.FACTION_CLAIMED_KEY);
                    return;
                }

                event.getPlayer().sendTitle((faction == FactionUtils.getFaction(event.getPlayer()) ? "§a" : "§c")
                        + faction.getDisplayName(), "§f"+faction.getMotd() + "", 10, 20, 10);
                playerData.display.alreadyDisplayedRegion = true;
            }
        }
    }
}
