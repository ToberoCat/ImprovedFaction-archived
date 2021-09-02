package io.github.toberocat.improvedfactions.utility;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ChunkUtils {
    public static final NamespacedKey FACTION_CLAIMED_KEY = new NamespacedKey(ImprovedFactionsMain.getPlugin(), "faction-claimed");

    public static Faction GetFactionClaimedChunk(Chunk chunk) {
        if (chunk == null) return null;
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        if (container.has(FACTION_CLAIMED_KEY, PersistentDataType.STRING)) {
            String factionRegistry = container.get(FACTION_CLAIMED_KEY, PersistentDataType.STRING);
            return FactionUtils.getFactionByRegistry(factionRegistry);
        }
        return null;
    }

    public static boolean UnClaimChunk(@NotNull Chunk chunk, @NotNull Faction faction) {
        PersistentDataContainer container = chunk.getPersistentDataContainer();

        if (!container.has(FACTION_CLAIMED_KEY, PersistentDataType.STRING) ||
        GetFactionClaimedChunk(chunk) != faction) {
            return false;
        }

        container.remove(FACTION_CLAIMED_KEY);
        return true;
    }

    public static boolean ClaimChunk(@NotNull Chunk chunk, @NotNull Faction faction) {

        PersistentDataContainer container = chunk.getPersistentDataContainer();

        if (container.has(FACTION_CLAIMED_KEY, PersistentDataType.STRING)) //Means: Chunk already claimed
            return false;
        container.set(FACTION_CLAIMED_KEY, PersistentDataType.STRING, faction.getRegistryName());
        return true;
    }
}
