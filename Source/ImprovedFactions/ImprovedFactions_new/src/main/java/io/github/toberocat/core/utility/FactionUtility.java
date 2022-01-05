package io.github.toberocat.core.utility;

import io.github.toberocat.core.utility.data.PersistentDataUtility;
import io.github.toberocat.core.utility.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

/**
 * Useful Faction functions
 */
public class FactionUtility {
    /**
     * Get if a (online) player is in a faction.
     * This will only check the Peristen data of this player for performance
     * If you want to iterate throw all factions, you must write is yourself
     * @param player The player you want to check
     * @return A boolean, if the player is in a faction
     */
    public static boolean isInFaction(Player player) {
        return PersistentDataUtility.Has(PersistentDataUtility.PLAYER_FACTION_REGISTRY, PersistentDataType.STRING, player.getPersistentDataContainer());
    }

    /**
     * Get the player faction a (online) player is in
     * @param player The player you want to get the faction from
     * @return The faction the player is in
     */
    public static Faction getPlayerFaction(Player player) {
        if (isInFaction(player)) return null;

        return PersistentDataUtility.Read(PersistentDataUtility.PLAYER_FACTION_REGISTRY, PersistentDataType.STRING, player.getPersistentDataContainer());
    }
}
