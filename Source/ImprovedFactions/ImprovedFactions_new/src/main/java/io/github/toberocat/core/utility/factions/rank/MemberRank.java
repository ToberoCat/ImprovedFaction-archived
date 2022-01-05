package io.github.toberocat.core.utility.factions.rank;

import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MemberRank extends Rank {
    public static final String registry = "Member";
    public MemberRank() {
        super("Member", registry, false);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8Members are people");
        } else if (line == 1) {
            return Language.format("&8who have joined your faction");
        }
        return "";
    }

    @Override
    public ItemStack getItem() {
        return Utility.createItem(Material.GRASS_BLOCK, getDisplayName());
    }
}
