package io.github.toberocat.core.utility.settings.type;

import io.github.toberocat.core.utility.factions.rank.Rank;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RankSetting extends Setting<String[]> {

    public RankSetting() {}

    public RankSetting(String[] allowedRanks, ItemStack display) {
        super(allowedRanks, display);
    }

    public boolean hasPermission(Rank rank) {
        return Arrays.stream(selected).anyMatch(x -> x.equals(rank.getRegistryName()));
    }
}
