package io.github.toberocat.improvedfactions.papi;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.factions.Faction;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class FactionExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "faction";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Tobero";
    }

    @Override
    public @NotNull String getVersion() {
        return ImprovedFactionsMain.getVERSION();
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("name")) {
            Faction faction = ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction;
            if (faction != null) {
                return faction.getDisplayName();
            }
            return ImprovedFactionsMain.getPlugin().getConfig().getString("general.noFactionPapi");
        }
        return null; // Placeholder is unknown by the Expansion
    }
}
