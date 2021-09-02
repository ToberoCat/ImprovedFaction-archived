package io.github.toberocat.improvedfactions.ranks;

import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.Utils;
import org.bukkit.inventory.ItemStack;

public class NewMemberRank extends Rank {
    public static final String registry = "NewMember";
    public NewMemberRank() {
        super("New member", registry, false);
    }

    @Override
    public String description() {
        return Language.format("&8New members are people who have joined your faction recently");
    }
}
