package io.github.toberocat.core.utility.factions.rank;

import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OwnerRank extends Rank{
    public static final String registry = "Owner";
    public OwnerRank() {
        super("Owner", registry, true);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8Owners have rights");
        } else if (line == 1) {
            return Language.format("&8to delete the faction");
        }
        return "";
    }

    @Override
    public ItemStack getItem() {
        return Utility.createItem(Material.GRASS_BLOCK, getDisplayName());
    }
}
