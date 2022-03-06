package io.github.toberocat.core.utility.factions.permission;

import io.github.toberocat.core.utility.factions.rank.members.*;
import io.github.toberocat.core.utility.items.ItemCore;
import io.github.toberocat.core.utility.settings.type.CallbackSettings;
import io.github.toberocat.core.utility.settings.type.EnumSetting;
import io.github.toberocat.core.utility.settings.FactionSettings;
import io.github.toberocat.core.utility.settings.type.RankSetting;
import io.github.toberocat.core.utility.settings.type.Setting;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class FactionPerm {
    public static final Map<String, RankSetting> DEFAULT_RANKS = new HashMap<>();

    private FactionSettings factionSettings;
    private Map<String, RankSetting> rankSetting;

    public FactionPerm() {
        this.factionSettings = new FactionSettings();
        rankSetting = new HashMap<>(DEFAULT_RANKS);
        Setting.populateSettings(FactionSettings.DEFAULT_SETTINGS, factionSettings.getFactionSettings());
    }

    public static void register()  {
        DEFAULT_RANKS.put("place", new RankSetting(new String[] {OwnerRank.registry, AdminRank.registry,
                ElderRank.registry, MemberRank.registry, ModeratorRank.registry
        }, ItemCore.create("place_perm", Material.BRICKS, "&ePlace permission")));

        DEFAULT_RANKS.put("break", new RankSetting(new String[] {OwnerRank.registry, AdminRank.registry,
                ElderRank.registry, MemberRank.registry, ModeratorRank.registry
        }, ItemCore.create("break_perm", Material.BRICKS, "&eBreak permission")));
    }

    public Map<String, Setting> getFactionSettings() {
        return factionSettings.getFactionSettings();
    }

    public void setFactionSettings(Map<String, Setting> factionSettings) {
        if (this.factionSettings == null) this.factionSettings = new FactionSettings();
        this.factionSettings.setFactionSettings(factionSettings);
        Setting.populateSettings(FactionSettings.DEFAULT_SETTINGS, factionSettings);
    }

    public Map<String, RankSetting> getRankSetting() {
        return rankSetting;
    }

    public void setRankSetting(Map<String, RankSetting> rankSetting) {
        this.rankSetting = rankSetting;
    }
}
