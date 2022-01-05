package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.TCallback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionUtils {

    public static Faction getFactionByRegistry(String _name) {
        String name = ChatColor.stripColor(_name);
        Faction fac = null;
        for (Faction faction : Faction.getFACTIONS()) {
            if (faction.getRegistryName().equals(name)) {
                fac = faction;
                break;
            }
        }
        return fac;
    }
    public static Faction getFaction(Player player) {
        for (Faction faction : Faction.getFACTIONS()) {
            for (FactionMember factionMember : faction.getMembers()) {
                if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                    return faction;
                }
            }
        }
        return null;
    }

    public static Faction getFaction(UUID player) {
        for (Faction faction : Faction.getFACTIONS()) {
            for (FactionMember factionMember : faction.getMembers()) {
                if (factionMember != null && factionMember.getUuid().equals(player)) {
                    return faction;
                }
            }
        }
        return null;
    }


    public static Rank getPlayerRank(Faction faction, Player player) {
        for (FactionMember factionMember : faction.getMembers()) {
            if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                return factionMember.getRank();
            }
        }
        return null;
    }

}
