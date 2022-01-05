package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.utility.*;
import io.github.toberocat.improvedfactions.utility.configs.DataManager;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.gui.Flag;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class Faction {
    private static List<Faction> FACTIONS = new ArrayList<Faction>();

    public static List<FactionData> data = new ArrayList<>();

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
    public static final String LIST_BANNED_PERMISSION = "listBanned";

    public static final String OPENTYPE_FLAG = "openType";
    public static final String RENAME_FLAG = "rename";
    public static final String MOTD = "motd";

    private int power;

    private String rules;

    private String displayName;
    private final String registryName;
    private FactionMember[] members;

    private String description = "A improved faction faction";
    private String motd = "";

    private int claimedchunks;

    private final FactionSettings settings;
    private List<UUID> bannedPeople;

    public Faction(ImprovedFactionsMain plugin, String displayName, OpenType openType) {
        this(displayName);
        members = new FactionMember[plugin.getConfig().getInt("factions.maxMembers")];
        Arrays.fill(members, null);

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

        bannedPeople = new ArrayList<>();
        settings = new FactionSettings();
        FACTIONS.add(this);
    }

    public String getDisplayName() {
        return displayName;
    }

    public FactionRankPermission getPermission(String perm) {
        return settings.getRanks().get(perm);
    }

    public FactionMember[] getMembers() {
        return members;
    }

    public boolean hasPermission(Player player, String permission) {
        FactionMember member = getFactionMember(player);

        if (member.getRank().isAdmin()) return true;

        FactionRankPermission perms = settings.getRanks().get(permission);
        return perms.getRanks().contains(member.getRank());
    }

    public FactionMember getFactionMember(Player player) {
        for (FactionMember factionMember : members) {
            if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                return factionMember;
            }
        }

        return null;
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

    public boolean Join(UUID uuid, Rank rank) {
        boolean success = false;
        for (int i = 0; i < members.length; i++) {
            if (members[i] == null) {
                members[i] = new FactionMember(uuid, rank);
                success = true;
                break;
            }
        }
        power += ImprovedFactionsMain.getPlugin().getConfig().getInt("faction.powerPerPlayer");
        if (power >= ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.maxClaimPower"))
            power = ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.maxClaimPower");
        return success;
    }

    public void ClaimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        if (claimedchunks >= power) {
            callback.Callback(null);
            return;
        }
        ChunkUtils.ClaimChunk(chunk, this, result -> {
            if (result.getClaimStatus() == ClaimStatus.Status.SUCCESS) {
                claimedchunks++;
            }
            callback.Callback(result);
        });
    }

    public void UnClaimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        ChunkUtils.UnClaimChunk(chunk, this, result -> {
            if (result.getClaimStatus() == ClaimStatus.Status.SUCCESS) claimedchunks--;
            callback.Callback(result);
        });
    }

    public boolean DeleteFaction() {
        for (FactionMember member : members) {
            if (member != null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
                ImprovedFactionsMain.getPlugin().getPlayerMessages().SendMessage(player,Language.getPrefix() + displayName + " got deleted. You left automatically");
            }
        }
        FACTIONS.remove(this);
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
                faction.settings.getFlags().put(rawFlags.get(i).split("::")[0], flags.get(i));
            }

            List<String> rawBanned = data.getConfig().getStringList("f." +key + ".banned");
            List<UUID> banned = new ArrayList<>();

            for (String rawBan : rawBanned) {
                try{
                    rawBan = rawBan.replace("]", "").replace("[", "");
                    banned.add(UUID.fromString(rawBan.trim()));
                } catch (IllegalArgumentException exception){
                    Debugger.LogWarning("&cCouldn't load banned");
                }
            }

            faction.claimedchunks = data.getConfig().getInt("f." +key + ".claimedChunks");
            faction.power = data.getConfig().getInt("f." +key + ".power");
            faction.bannedPeople = banned;
            faction.setMotd(data.getConfig().getString("f." +key + ".motd"));
            faction.setDescription(data.getConfig().getString("f." +key + ".description"));
            for (String perm : data.getConfig().getStringList("f." +key + ".settings.permissions")) {
                String[] parms = perm.split("::");
                faction.settings.getRanks().put(parms[0], FactionRankPermission.fromString(perm));
            }

            for (FactionData dat : Faction.data) {
                dat.Load(faction, data);
            }
        }
    }

    public static void SaveFactions(ImprovedFactionsMain plugin) {
        DataManager data = plugin.getFactionData();
        data.getConfig().set("f", null);
        data.saveConfig();
        for (Faction faction : FACTIONS) {
            List<String> _members = new ArrayList<String>();
            for (FactionMember factionMember : faction.getMembers()) {
                if (factionMember != null)
                    _members.add(factionMember.toString());
            }

            List<String> _permissions = new ArrayList<>();
            for (String key : faction.settings.getRanks().keySet()) {
                FactionRankPermission permission = faction.getPermission(key);
                _permissions.add(key + "::" + permission.toString());
            }

            List<String> flags = new ArrayList<>();
            for (String key : faction.settings.getFlags().keySet()) {
                flags.add(key + "::" + faction.settings.getFlags().get(key).toString());
            }

            data.getConfig().set("f." + faction.getRegistryName() + ".displayName", faction.getDisplayName());
            data.getConfig().set("f." + faction.getRegistryName() + ".description", faction.getDescription());
            data.getConfig().set("f." + faction.getRegistryName() + ".motd", faction.getMotd());
            data.getConfig().set("f." + faction.getRegistryName() + ".power", faction.getPower());
            data.getConfig().set("f." + faction.getRegistryName() + ".claimedChunks", faction.getClaimedchunks());
            data.getConfig().set("f." + faction.getRegistryName() + ".settings.flags", flags);
            data.getConfig().set("f." + faction.getRegistryName() + ".settings.permissions", _permissions);
            data.getConfig().set("f." + faction.getRegistryName() + ".members", _members.toArray());
            data.getConfig().set("f." + faction.getRegistryName() + ".banned", Utils.listToStringList(faction.bannedPeople));

            data.saveConfig();
            for (FactionData dat : Faction.data) {
                dat.Save(faction, data);
            }
        }
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<UUID> getBannedPeople() {
        return bannedPeople;
    }

    public void setBannedPeople(List<UUID> bannedPeople) {
        this.bannedPeople = bannedPeople;
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

    public void setClaimedchunks(int claimedchunks) {
        this.claimedchunks = claimedchunks;
    }

    public void setMembers(FactionMember[] members) {
        this.members = members;
    }

    public FactionSettings getSettings() {
        return settings;
    }

    public static List<Faction> getFACTIONS() {
        return FACTIONS;
    }

    public static void setFACTIONS(List<Faction> FACTIONS) {
        Faction.FACTIONS = FACTIONS;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
