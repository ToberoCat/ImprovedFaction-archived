package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.gui.Flag;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.Callback;
import io.github.toberocat.improvedfactions.utility.SignMenuFactory;
import io.github.toberocat.improvedfactions.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FactionSettings {

    public static Map<String, Flag> FLAGS = new HashMap<>();
    public static Map<String, FactionRankPermission> RANKS = new HashMap<>();

    private Map<String, Flag> flags;
    private Map<String, FactionRankPermission> ranks;

    public static void Init() {
        RANKS.put(Faction.CLAIM_CHUNK_PERMISSION, new FactionRankPermission(Utils.createItem(Material.SHIELD, Language.format("&aClaim chunk"), null), new Rank[] {
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        RANKS.put(Faction.UNCLAIM_CHUNK_PERMISSION, new FactionRankPermission(Utils.createItem(Material.WOODEN_AXE, Language.format("&aUnclaim chunk"), null), new Rank[] {
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        RANKS.put(Faction.INVITE_PERMISSION, new FactionRankPermission(Utils.createItem(Material.NAME_TAG, Language.format("&aInvite others"), null), new Rank[] {
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        RANKS.put(Faction.BUILD_PERMISSION, new FactionRankPermission(Utils.createItem(Material.BRICKS, Language.format("&aBuild permission"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        RANKS.put(Faction.BREAK_PERMISSION, new FactionRankPermission(Utils.createItem(Material.WOODEN_PICKAXE, Language.format("&aBreak permission"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        RANKS.put(Faction.LIST_BANNED_PERMISSION, new FactionRankPermission(Utils.createItem(Material.BARRIER, Language.format("&aList banned"), null), new Rank[] {
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));

        FLAGS.put(Faction.OPENTYPE_FLAG, new Flag(Flag.FlagType.Enum, Material.END_PORTAL_FRAME, "&aOpenType",
                Arrays.asList(Faction.OpenType.values()), Faction.OpenType.Private.ordinal()));

        FLAGS.put(Faction.RENAME_FLAG, new Flag(Flag.FlagType.Function, Material.OAK_SIGN, "&aRename faction",
                "&8Rename your faction", new Callback() {
            @Override
            public boolean CallBack(Faction faction, Player player, Object object) {
                SignMenuFactory.Menu menu = ImprovedFactionsMain.getPlugin().getSignMenuFactory().newMenu(Arrays.asList(faction.getDisplayName()))
                        .reopenIfFail(true)
                        .response((p, strings) -> {
                            faction.setDisplayName(Language.format(strings[0]));
                            return true;
                        });
                menu.open(player);
                return false;
            }
        }));
        FLAGS.put(Faction.MOTD, new Flag(Flag.FlagType.Function, Material.BIRCH_SIGN, "&aFaction motd",
                "&8Set your faction motd", new Callback() {
            @Override
            public boolean CallBack(Faction faction, Player player, Object object) {
                SignMenuFactory.Menu menu = ImprovedFactionsMain.getPlugin().getSignMenuFactory().newMenu(Arrays.asList(faction.getMotd()))
                        .reopenIfFail(true)
                        .response((p, strings) -> {
                            faction.setMotd(Language.format(strings[0]));
                            return true;
                        });
                menu.open(player);
                return false;
            }
        }));

        for (String key : FLAGS.keySet()) {
            ImprovedFactionsMain.getPlugin().getConfig().addDefault("factions.flags." + key, true);
        }
        for (String key : RANKS.keySet()) {
            ImprovedFactionsMain.getPlugin().getConfig().addDefault("factions.permissions." + key, true);
        }

        ImprovedFactionsMain.getPlugin().getConfig().options().copyDefaults(true);
        ImprovedFactionsMain.getPlugin().saveConfig();
    }

    public FactionSettings() {
        flags = new HashMap<>();
        ranks = new HashMap<>();

        FileConfiguration config = ImprovedFactionsMain.getPlugin().getConfig();

        for (String key : FLAGS.keySet()) {
            Flag flag = FLAGS.get(key);
            if (config.getBoolean("factions.flags." + key)) {
                flags.put(key, flag);
            }
        }
        for (String key : RANKS.keySet()) {
            FactionRankPermission permission = RANKS.get(key);
            if (config.getBoolean("factions.permissions." + key)) {
                ranks.put(key, permission);
            }
        }
    }

    public Map<String, Flag> getFlags() {
        return flags;
    }

    public Map<String, FactionRankPermission> getRanks() {
        return ranks;
    }
}
