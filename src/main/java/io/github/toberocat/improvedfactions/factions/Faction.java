package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.DataManager;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.gui.Flag;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.Callback;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import io.github.toberocat.improvedfactions.utility.SignMenuFactory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Faction {


    public enum OpenType {
        Public("&aPublic"),
        Private("&cPrivate");

        private final String display;
        OpenType(String display) {
            this.display = Language.format(display);
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public static final String CLAIM_CHUNK_PERMISSION = "claim_chunk";
    public static final String UNCLAIM_CHUNK_PERMISSION = "unclaim_chunk";
    public static final String INVITE_PERMISSION = "invite";
    public static final String BUILD_PERMISSION = "build";
    public static final String BREAK_PERMISSION = "break";

    public static final String OPENTYPE_FLAG = "openType";
    public static final String ALLOW_CRYSTAL_BREAK_FLAG = "allowEndCrystalBreak";
    public static final String RENAME_FLAG = "rename";
    public static final String MOTD = "motd";

    public static FactionFlag[] FLAGS = new FactionFlag[] {
            new FactionFlag(OPENTYPE_FLAG, new Flag(Flag.FlagType.Enum, Material.END_PORTAL_FRAME, "&aOpenType",
                            Arrays.asList(OpenType.values()), OpenType.Public.ordinal())),
            new FactionFlag(ALLOW_CRYSTAL_BREAK_FLAG, new Flag(Flag.FlagType.Boolean, Material.END_CRYSTAL, "&aCrystal break",
                    false)),
            new FactionFlag(RENAME_FLAG, new Flag(Flag.FlagType.Function, Material.OAK_SIGN, "&aRename faction",
                    "&8Rename your faction", new Callback() {
                @Override
                public void CallBack(Faction faction, Player player) {
                    SignMenuFactory.Menu menu = ImprovedFactionsMain.getPlugin().getSignMenuFactory().newMenu(Arrays.asList(faction.getDisplayName()))
                            .reopenIfFail(true)
                            .response((p, strings) -> {
                                faction.setDisplayName(Language.format(strings[0]));
                                return true;
                            });
                    menu.open(player);
                }
            })),
            new FactionFlag(MOTD, new Flag(Flag.FlagType.Function, Material.BIRCH_SIGN, "&aFaction motd",
                    "&8Set your faction motd", new Callback() {
                @Override
                public void CallBack(Faction faction, Player player) {
                    SignMenuFactory.Menu menu = ImprovedFactionsMain.getPlugin().getSignMenuFactory().newMenu(Arrays.asList(faction.getMotd()))
                            .reopenIfFail(true)
                            .response((p, strings) -> {
                                faction.setMotd(Language.format(strings[0]));
                                return true;
                            });
                    menu.open(player);
                }
            }))
    };


    private int power;

    private static List<Faction> factions = new ArrayList<Faction>();

    private String displayName;
    private final String registryName;
    private FactionMember[] members;

    private String description = "A improved faction faction";
    private String motd = "";

    private int claimedchunks;

    private Map<String, Flag> flags = new HashMap<>();

    private Map<String, FactionRankPermission> permissions = new HashMap<>();


    //? Next: Faction nick names; Faction openType: Public (You see it in /f join) Private (Only accessable by invites from admin)
    //? Personal (Only accessable by invites from admin or join invites. Not visible in /f join)

    public Faction(ImprovedFactionsMain plugin, String displayName, OpenType openType) {
        this(displayName);
        members = new FactionMember[plugin.getConfig().getInt("factions.maxMembers")];
        Arrays.fill(members, null);

        permissions.put(CLAIM_CHUNK_PERMISSION, new FactionRankPermission(new Rank[] {
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        permissions.put(UNCLAIM_CHUNK_PERMISSION, new FactionRankPermission(new Rank[] {
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        permissions.put(INVITE_PERMISSION, new FactionRankPermission(new Rank[] {
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        permissions.put(BUILD_PERMISSION, new FactionRankPermission(new Rank[] {
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
        permissions.put(BREAK_PERMISSION, new FactionRankPermission(new Rank[] {
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));

        power = ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.startClaimPower");

    }
    public Faction(String displayName, FactionMember[] members) {
        this(displayName);
        this.members = members;

    }

    private Faction(String displayName) {
        this.displayName = displayName;
        this.registryName = ChatColor.stripColor(displayName);
        claimedchunks = 0;

        for (FactionFlag factionFlag : FLAGS) {
            flags.put(factionFlag.getId(), factionFlag.getFlag());
        }

        factions.add(this);
    }


    public static List<Faction> getFactions() {
        return factions;
    }
    public String getDisplayName() {
        return displayName;
    }

    public FactionRankPermission getPermission(String perm) {
        if (!permissions.containsKey(perm)) {
            permissions.put(perm, new FactionRankPermission(new Rank[] {
                    Rank.fromString(MemberRank.registry)
            }));
        }
        return permissions.get(perm);
    }

    public FactionMember[] getMembers() {
        return members;
    }

    public Flag getFlag(String key) {
        return flags.get(key);
    }

    public boolean hasPermission(Player player, String permission) {
        for (FactionMember factionMember : members) {
            if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                if (permissions.containsKey(permission)) {
                    FactionRankPermission perms = permissions.get(permission);
                    if (factionMember.getRank().isAdmin() || perms.getRanks().contains(factionMember.getRank())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasMaxMembers() {
        for (FactionMember member : members) {
            if (member == null) return false;
        }
        return true;
    }

    public void SetRank(Player player, Rank rank) {
        for (FactionMember factionMember : members) {
            if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                factionMember.setRank(rank);
            }
        }
    }

    public boolean Join(Player player, Rank rank) {
        boolean success = false;
        for (int i = 0; i < members.length; i++) {
            if (members[i] == null) {
                members[i] = new FactionMember(player.getUniqueId(), rank);
                success = true;
                break;
            }
        }
        ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction = this;
        power += ImprovedFactionsMain.getPlugin().getConfig().getInt("faction.powerPerPlayer");
        if (power >= ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.maxClaimPower"))
            power = ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.maxClaimPower");
        return success;
    }

    public boolean ClaimChunk(Chunk chunk) {
        if (claimedchunks >= power) return false;
        boolean result = ChunkUtils.ClaimChunk(chunk, this);
        if (result) claimedchunks++;
        return result;
    }

    public boolean UnClaimChunk(Chunk chunk) {
        boolean result = ChunkUtils.UnClaimChunk(chunk, this);
        if (result) claimedchunks--;
        return result;
    }

    public boolean DeleteFaction() {
        for (FactionMember member : members) {
            if (member != null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
                ImprovedFactionsMain.getPlugin().getPlayerMessages().SendMessage(player,Language.getPrefix() + displayName + " got deleted. You left automatically");
            }
        }
        factions.remove(this);
        return true;
    }


    public boolean Leave(Player player) {
        for (int i = 0; i < members.length; i++) {
            if (members[i] != null && members[i].getUuid().equals(player.getUniqueId())) {
                members[i] = null;
                return true;
            }
        }
        ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction = null;
        power -= ImprovedFactionsMain.getPlugin().getConfig().getInt("faction.powerPerPlayer");
        return false;
    }

    public boolean Leave(OfflinePlayer player) {
        for (int i = 0; i < members.length; i++) {
            if (members[i] != null && members[i].getUuid().equals(player.getUniqueId())) {
                members[i] = null;
                return true;
            }
        }
        ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction = null;
        return false;
    }

    public static void LoadFactions(ImprovedFactionsMain plugin) {
        DataManager data = plugin.getFactionData();
        if (data.getConfig().getConfigurationSection("f") == null) return;
        for (String key : data.getConfig().getConfigurationSection("f").getKeys(false)) {
            String displayName = ChatColor.translateAlternateColorCodes('&', data.getConfig().getString("f." +key + ".displayName"));

            FactionMember[] members = new FactionMember[plugin.getConfig().getInt("factions.maxMembers")];
            Arrays.fill(members, null);

            List<String> raw = data.getConfig().getStringList("f." +key + ".members");
            for (int i = 0; i < raw.size(); i++) {
                String rawMember = raw.get(i);
                members[i] = FactionMember.fromString(rawMember);
            }

            List<String> rawFlags = data.getConfig().getStringList("f." +key + ".settings.flags");
            List<Flag> flags = new ArrayList<>();
            for (String str : rawFlags) {
                flags.add(Flag.fromString(str));
            }

            Faction faction = new Faction(displayName, members);

            for (int i = 0; i < flags.size(); i++) {
                faction.getFlags().put(rawFlags.get(i).split("::")[0], flags.get(i));
            }
            faction.claimedchunks = data.getConfig().getInt("f." +key + ".claimedChunks");
            faction.power = data.getConfig().getInt("f." +key + ".power");
            faction.setMotd(data.getConfig().getString("f." +key + ".motd"));
            faction.setDescription(data.getConfig().getString("f." +key + ".description"));
            for (String perm : data.getConfig().getStringList("f." +key + ".settings.permissions")) {
                String[] parms = perm.split("::");
                faction.getPermissions().put(parms[0], FactionRankPermission.fromString(parms[1]));
            }
        }
    }

    public static void SaveFactions(ImprovedFactionsMain plugin) {
        DataManager data = plugin.getFactionData();
        data.getConfig().set("f", null);
        data.saveConfig();
        for (Faction faction : factions) {
            List<String> _members = new ArrayList<String>();
            for (FactionMember factionMember : faction.getMembers()) {
                if (factionMember != null)
                    _members.add(factionMember.toString());
            }

            List<String> _permissions = new ArrayList<>();
            for (String key : faction.permissions.keySet()) {
                FactionRankPermission permission = faction.getPermission(key);
                _permissions.add(key + "::" + permission.toString());
            }

            List<String> flags = new ArrayList<>();
            for (String key : faction.flags.keySet()) {
                flags.add(key + "::" + faction.flags.get(key).toString());
            }

            data.getConfig().set("f." + faction.getRegistryName() + ".displayName", faction.getDisplayName());
            data.getConfig().set("f." + faction.getRegistryName() + ".description", faction.getDescription());
            data.getConfig().set("f." + faction.getRegistryName() + ".motd", faction.getMotd());
            data.getConfig().set("f." + faction.getRegistryName() + ".power", faction.getPower());
            data.getConfig().set("f." + faction.getRegistryName() + ".claimedChunks", faction.getClaimedchunks());
            data.getConfig().set("f." + faction.getRegistryName() + ".settings.flags", flags);
            data.getConfig().set("f." + faction.getRegistryName() + ".settings.permissions", _permissions);
            data.getConfig().set("f." + faction.getRegistryName() + ".members", _members.toArray());
        }
        data.saveConfig();
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getClaimedchunks() {
        return claimedchunks;
    }

    public String getRegistryName() {
        return registryName;
    }

    public Map<String, FactionRankPermission> getPermissions() {
        return permissions;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, Flag> getFlags() {
        return flags;
    }

    public void setPermissions(Map<String, FactionRankPermission> permissions) {
        this.permissions = permissions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }
}
