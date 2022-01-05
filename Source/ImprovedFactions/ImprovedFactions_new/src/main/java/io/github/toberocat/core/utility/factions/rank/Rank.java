package io.github.toberocat.core.utility.factions.rank;

import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Rank {

    public static List<Rank> ranks = new ArrayList<>();

    private String displayName;
    private final String registryName;
    private boolean isAdmin;

    public Rank(String displayName, String registryName, boolean isAdmin) {
        this.displayName = displayName;
        this.registryName = registryName;
        this.isAdmin = isAdmin;
        ranks.add(this);
    }

    public static void Init() {
        new AdminRank();
        new MemberRank();
        new OwnerRank();
        new NewMemberRank();
    }

    public String[] getDescription() {
        int maxlines = 3;
        String[] lines = new String[maxlines+1];
        for (int i = 0; i < maxlines; i++) {
            String line = Language.format(description(i));
            if (!line.isEmpty()) {
                lines[i] = line;
            }
        }
        return lines;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRegistryName() {
        return registryName;
    }

    public abstract String description(int line);

    public ItemStack getItem() {
        return Utility.createItem(Material.GRASS_BLOCK, getDisplayName());
    }

    @Override
    public String toString() {
        return registryName;
    }

    public static Rank fromString(String str) {
        for (Rank rank : ranks) {
            if (rank.toString().equals(str)) return rank;
        }
        return null;
    }
}
