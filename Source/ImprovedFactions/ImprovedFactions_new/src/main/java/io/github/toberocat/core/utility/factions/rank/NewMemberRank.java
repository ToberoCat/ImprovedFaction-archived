package io.github.toberocat.core.utility.factions.rank;

import io.github.toberocat.core.utility.language.Language;

public class NewMemberRank extends Rank {
    public static final String registry = "NewMember";
    public NewMemberRank() {
        super("New member", registry, false);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8New members are people");
        } else if (line == 1) {
            return Language.format("&8who have joined your faction recently");
        }
        return "";
    }
}
