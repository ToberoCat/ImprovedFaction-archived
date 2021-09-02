package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class FactionUtils {

    public static Faction getFactionByRegistry(String _name) {
        String name = ChatColor.stripColor(_name);
        for (Faction faction : Faction.getFactions()) {
            if (faction.getRegistryName().equals(name))
                return faction;
        }
        return null;
    }
    public static Faction getFaction(Player player) {
        for (Faction faction : Faction.getFactions()) {
            for (FactionMember factionMember : faction.getMembers()) {
                if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
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
