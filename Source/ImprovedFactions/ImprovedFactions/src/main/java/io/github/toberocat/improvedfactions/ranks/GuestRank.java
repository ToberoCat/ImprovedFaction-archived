package io.github.toberocat.improvedfactions.ranks;

import io.github.toberocat.improvedfactions.language.Language;

public class GuestRank extends Rank {
    public static final String registry = "guest";
    public GuestRank() {
        super("Guest rank", registry, false);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8They are everyone");
        } else if (line == 1) {
            return Language.format("&8whom is not in your faction / ally");
        }
        return "";
    }
}
