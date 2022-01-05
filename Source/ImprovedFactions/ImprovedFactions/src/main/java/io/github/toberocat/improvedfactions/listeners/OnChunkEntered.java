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
import io.github.toberocat.improvedfactions.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class OnChunkEntered implements Listener {


    @EventHandler
    public void ChunkEnter(OnChunkEnterEvent event) {
        PersistentDataContainer container = event.getChunk().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(ImprovedFactionsMain.getPlugin(),
                "faction-claimed");
        PlayerData playerData = ImprovedFactionsMain.playerData.get(event.getPlayer().getUniqueId());

        if (UnclaimAutoChunkSubCommand.autoUnclaim.contains(event.getPlayer().getUniqueId()) && playerData.playerFaction != null) {
            Utils.UnClaimChunk(event.getPlayer());
        }

        if (ClaimAutoChunkSubCommand.autoClaim.contains(event.getPlayer().getUniqueId()) && playerData.playerFaction != null) {
            Utils.ClaimChunk(event.getPlayer());
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
                if  (Objects.equals(ImprovedFactionsMain.getPlugin().getConfig().getString("general.messageType"), "TITLE")) {
                    event.getPlayer().sendTitle("§2Wildness", "", 10, 20, 10);
                } else if (Objects.equals(ImprovedFactionsMain.getPlugin().getConfig().getString("general.messageType"), "ACTIONBAR")) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§2Wildness"));
                } else {
                    event.getPlayer().sendMessage("Couldn't find the type " + ImprovedFactionsMain.getPlugin().getConfig().getString("general.messageType") + "\n"
                            + "Valid types = { TITLE, ACTIONBAR }");
                }
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

                    if  (Objects.equals(ImprovedFactionsMain.getPlugin().getConfig().getString("general.messageType"), "TITLE")) {
                        event.getPlayer().sendTitle((faction == FactionUtils.getFaction(event.getPlayer()) ? "§a" : "§c")
                                + faction.getDisplayName(), "§f"+(faction.getMotd() == null ? "" : faction.getMotd()), 10, 20, 10);
                    } else if (Objects.equals(ImprovedFactionsMain.getPlugin().getConfig().getString("general.messageType"), "ACTIONBAR")) {
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((faction == FactionUtils.getFaction(event.getPlayer()) ? "§a" : "§c")
                                + faction.getDisplayName()));
                    } else {
                        event.getPlayer().sendMessage("Couldn't find the type " + ImprovedFactionsMain.getPlugin().getConfig().getString("general.messageType") + "\n"
                                + "Valid types = { TITLE, ACTIONBAR }");
                    }
                    playerData.display.alreadyDisplayedRegion = true;
            }
        }
    }
}
