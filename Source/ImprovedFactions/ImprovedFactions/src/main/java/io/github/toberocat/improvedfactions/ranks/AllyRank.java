package io.github.toberocat.improvedfactions.ranks;

import io.github.toberocat.improvedfactions.language.Language;

public class AllyRank extends Rank{
    public AllyRank() {
        super("Ally rank", "allyrank", false);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8Allies are your");
        } else if (line == 1) {
            return Language.format("&8factions friends");
        }
        return "";
    }
}
