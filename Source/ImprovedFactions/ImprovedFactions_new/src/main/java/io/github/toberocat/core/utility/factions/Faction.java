package io.github.toberocat.core.utility.factions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.data.DataAccess;
import io.github.toberocat.core.utility.events.faction.*;
import io.github.toberocat.core.utility.factions.bank.FactionBank;
import io.github.toberocat.core.utility.factions.members.FactionMemberManager;
import io.github.toberocat.core.utility.factions.permission.FactionPerm;
import io.github.toberocat.core.utility.factions.power.PowerManager;
import io.github.toberocat.core.utility.factions.rank.members.OwnerRank;
import io.github.toberocat.core.utility.factions.rank.Rank;
import io.github.toberocat.core.utility.factions.relation.RelationManager;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.language.Parseable;
import io.github.toberocat.core.utility.messages.MessageSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Faction {

    private static final Map<String, Faction> LOADED_FACTIONS = new HashMap<>();

    public enum OpenType { PUBLIC, INVITE_ONLY, CLOSED }

    private PowerManager powerManager;
    private FactionMemberManager factionMemberManager;
    private RelationManager relationManager;
    private FactionBank factionBank;
    private FactionPerm factionPerm;

    private OpenType openType;
    private String displayName, registryName, motd;
    private String[] description;
    private boolean frozen, permanent;
    private String createdAt;

    private UUID owner;

    private int claimedChunks;

    /**
     * It creates a new Faction object, and adds it to the LOADED_FACTIONS map
     *
     * @param displayName The name of the faction.
     * @param owner The player who created the faction
     * @return A Result object.
     */
    public static Result<Faction> CreateFaction(String displayName, Player owner) {
        if (displayName.length() >= (Integer) MainIF.getConfigManager().getValue("faction.maxNameLen"))
            return Result.failure("OVER_MAX_LEN", "You reached the maximum length for a faction name");
        String registryName = ChatColor.stripColor(displayName.replaceAll("[^a-zA-Z0-9]", " "));

        if (MainIF.getConfigManager().getValue("forbidden.checkFactionNames")) {
            Result result = AsyncCore.Run(() -> {
                ArrayList<String> forbiddenNames =
                        MainIF.getConfigManager().getValue("forbidden.factionNames");

                String checkRegistry = MainIF.getConfigManager().getValue("forbidden.checkLeetspeak") ?
                        Language.simpleLeetToEnglish(registryName) : registryName;

                if (forbiddenNames.contains(checkRegistry))
                    return new Result(false).setMessages("FORBIDDEN_NAME",
                        "Sorry, but this name is forbidden");

                double disbandPercent = MainIF.getConfigManager().getValue("forbidden.disbandAtPercent");
                double reportPercent = MainIF.getConfigManager().getValue("forbidden.reportAtPercent");

                disbandPercent /= 100;
                reportPercent /= 100;

                for (String forbidden : forbiddenNames) {
                    double prediction = Language.similarity(forbidden, checkRegistry);

                    if (prediction > disbandPercent) {
                        return new Result(false).setMessages("FORBIDDEN_NAME",
                                "Sorry, but this name is forbidden");
                    } else if (prediction > reportPercent) {
                        ArrayList<String> reportCommands = MainIF.getConfigManager().getValue("commands.forbidden");

                        MainIF.getIF().getServer().getScheduler().runTaskLater(MainIF.getIF(), () -> {
                            for (String command : reportCommands) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                        Language.parse(command, new Parseable[]{
                                                new Parseable("{word}", registryName),
                                                new Parseable("{similar}", forbidden),
                                                new Parseable("{player_name}", owner.getName()),
                                                new Parseable("{player_uuid}", owner.getUniqueId().toString()),
                                                new Parseable("{task}", "FACTION_CREATION"),
                                                new Parseable("{similarityPer}", prediction*100+"")
                                        }));
                            }
                        }, 0);
                        return new Result(false).setMessages("MAYBE_FORBIDDEN",
                                "Your faction name is very similar to a forbidden word. If you think your name is fine, just ignore it. If you want to retreat the report, just change the name to something appropriate");
                    }
                }

                return new Result(true);
            }).await().getResult();

            if (!result.isSuccess()) return result;
        }
        Faction newFaction = new Faction(displayName, registryName, owner.getUniqueId(), OpenType.PUBLIC);
        boolean canCreate = Utility.callEvent(new FactionCreateEvent(newFaction, owner));
        if (!canCreate) {
            DataAccess.removeFile("Factions", registryName);

            return new Result<Faction>(false).setMessages("EVENT_CANCEL",
                    "Couldn't create your faction");
        }

        LOADED_FACTIONS.put(registryName, newFaction);
        Result result = newFaction.join(owner, Rank.fromString(OwnerRank.registry));

        if (!result.isSuccess()) {
            newFaction.delete();
        }

        return result.setPaired(newFaction);
    }

    /**
     * Don't use this. iT's for jackson (json).
     */
    public Faction() {
    }

    private Faction(String displayName, String registryName, UUID owner, OpenType openType) {
        super();
        this.openType = openType;
        this.registryName = registryName;
        this.displayName = displayName;

        this.powerManager = new PowerManager(this,
               MainIF.getConfigManager().getValue("power.maxDefaultFaction"));
        this.factionMemberManager = new FactionMemberManager(this);
        this.relationManager = new RelationManager(this);
        this.factionBank = new FactionBank();
        this.factionPerm = new FactionPerm();

        this.frozen = false;
        this.permanent = Boolean.TRUE.equals(MainIF.getConfigManager().getValue("faction.permanent"));
        this.description = new String[] { "&eCool &cfaction" };
        this.motd = "New faction";
        this.claimedChunks = 0;
        this.owner = owner;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        this.createdAt = fmt.print(new LocalDateTime());
    }

    /**
     * Let a player join a faction
     * @param player
     * @param rank
     * @return
     */
    public Result join(Player player, Rank rank) {
        if (frozen) return Result.failure("FROZEN", "This faction is frozen. You can't join");

        boolean canJoin = Utility.callEvent(new FactionJoinEvent(this, player));
        if (!canJoin)  return Result.failure("EVENT_CANCEL",
                "Couldn't join faction");

        return factionMemberManager.join(player);
    }

    /**
     * Let a player leave the faction
     *
     * @param player The player who is leaving the faction
     * @return A Result object.
     */
    public Result leave(Player player) {
        if (frozen) return Result.failure("FROZEN", "This faction is frozen. You can't leave");

        boolean canLeave = Utility.callEvent(new FactionLeaveEvent(this, player));
        if (!canLeave)  return Result.failure("EVENT_CANCEL",
                "Couldn't leave your faction");

        Language.sendMessage(LangMessage.COMMAND_FACTION_LEAVE_SUCCESS, player);

        return factionMemberManager.leave(player);
    }

    /**
     * Kick a player from the faction
     *
     * @param player The player to kick
     * @return A Result object.
     */
    public Result kick(OfflinePlayer player) {
        if (frozen) return Result.failure("FROZEN", "This faction is frozen. You can't kick");

        boolean canKick = Utility.callEvent(new FactionKickEvent(this, player));
        if (!canKick)  return Result.failure("EVENT_CANCEL",
                "Couldn't kick §e" + player.getName());

        if (player.isOnline()) {
            Language.sendMessage(LangMessage.FACTION_KICKED, player.getPlayer(),
                    new Parseable("{faction_display}", displayName));
        } else {
            MessageSystem.sendMessage(player.getUniqueId(), Language.getMessage(LangMessage.FACTION_KICKED,
                    "en_us", new Parseable("{faction_display}", displayName)));
        }

        Result result = factionMemberManager.kick(player.getUniqueId());

        if (!result.isSuccess()) return result;
        return new Result(true).setMessages("KICKED", "You kicked &e" + player.getName());
    }

    /**
     * Ban a player from the faction
     *
     * @param player The player to ban
     * @return A Result object.
     */
    public Result ban(OfflinePlayer player) {
        if (frozen) return Result.failure("FROZEN", "This faction is frozen. You can't ban");

        boolean canKick = Utility.callEvent(new FactionBanEvent(this, player));
        if (!canKick)  return Result.failure("EVENT_CANCEL",
                "Couldn't ban §e" + player.getName());

        if (player.isOnline()) {
            Language.sendMessage(LangMessage.FACTION_KICKED, player.getPlayer());
        } else {
            MessageSystem.sendMessage(player.getUniqueId(), Language.getMessage(LangMessage.FACTION_KICKED, "en_us"));
        }

        Result result = factionMemberManager.ban(player.getUniqueId());

        if (!result.isSuccess()) return result;
        return new Result(true).setMessages("BANNED", "You banned &e" + player.getName());
    }

    /**
     * This function will unban a player from the faction
     *
     * @param player The player to unban
     * @return A Result object.
     */
    public Result unban(OfflinePlayer player) {
        if (frozen) return Result.failure("FROZEN", "This faction is frozen. You can't unban");

        boolean canKick = Utility.callEvent(new FactionUnbanEvent(this, player));
        if (!canKick)  return Result.failure("EVENT_CANCEL",
                "Couldn't unban §e" + player.getName());

        return factionMemberManager.pardon(player.getUniqueId());
    }

    /**
     * This function kicks all members of the faction and then removes the faction from the LOADED_FACTIONS list
     *
     * @return A Result object.
     */
    public Result delete() {
        if (frozen) return Result.failure("FROZEN", "This faction is frozen. You can't kick");
        AsyncCore.Run(() -> {
            boolean canDelete = Utility.callEvent(new FactionDeleteEvent(this));
            if (!canDelete) return;

            ArrayList<UUID> memberCopy = new ArrayList<>(factionMemberManager.getMembers());
            for (UUID member : memberCopy) {
                kick(Bukkit.getOfflinePlayer(member));
            }

            LOADED_FACTIONS.remove(registryName);
            DataAccess.removeFile("Factions", registryName);
        });
        return Result.success();
    }

    //<editor-fold desc="Getters and Setters">
    public static Map<String, Faction> getLoadedFactions() {
        return LOADED_FACTIONS;
    }
    public OpenType getOpenType() {
        return openType;
    }

    public void setOpenType(OpenType openType) {
        this.openType = openType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public PowerManager getPowerManager() {
        return powerManager;
    }

    public void setPowerManager(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    public FactionMemberManager getFactionMemberManager() {
        return factionMemberManager;
    }

    public Faction setFactionMemberManager(FactionMemberManager factionMemberManager) {
        this.factionMemberManager = factionMemberManager;
        return this;
    }

    public RelationManager getRelationManager() {
        return relationManager;
    }

    public void setRelationManager(RelationManager relationManager) {
        this.relationManager = relationManager;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getClaimedChunks() {
        return claimedChunks;
    }

    public void setClaimedChunks(int claimedChunks) {
        this.claimedChunks = claimedChunks;
    }

    public FactionBank getFactionBank() {
        return factionBank;
    }

    public void setFactionBank(FactionBank factionBank) {
        this.factionBank = factionBank;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public FactionPerm getFactionPerm() {
        return factionPerm;
    }

    public void setFactionPerm(FactionPerm factionPerm) {
        this.factionPerm = factionPerm;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    //</editor-fold>
}
