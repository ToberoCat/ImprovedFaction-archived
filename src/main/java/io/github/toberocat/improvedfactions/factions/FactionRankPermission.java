package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.ranks.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactionRankPermission {

    private List<Rank> ranks;

    public FactionRankPermission(Rank[] ranks) {
        this.ranks = new ArrayList<>(Arrays.asList(ranks));
    }

    @Override
    public String toString() {
        return "FactionRankPermission{" +
                "ranks=" + ranks +
                '}';
    }

    public static FactionRankPermission fromString(String string) {
        string = string.replace("FactionRankPermission{", "");
        string = string.replace("}", "");

        Rank[] ranks = null;

        String[] parms = string.split("[=]");
        for (int i = 0;  i < parms.length; i++) {
            String parm = parms[i];
            if (parm.equals("ranks")) {
                String[] items = parms[i + 1].split(", ");
                List<Rank> _ranks = new ArrayList<>();
                for (String item : items) {
                    String value = item.replace("[", "").replace("]", "");
                    _ranks.add(Rank.fromString(value));
                }
                ranks = new Rank[_ranks.size()];
                ranks = _ranks.toArray(ranks);
            }
        }
        return new FactionRankPermission(ranks);
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public void setRanks(List<Rank> ranks) {
        this.ranks = ranks;
    }
}
