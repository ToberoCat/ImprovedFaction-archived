package io.github.toberocat.core.utility.factions.members;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.listeners.PlayerJoinListener;
import io.github.toberocat.core.utility.FactionUtility;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.data.PersistentDataUtility;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This is for managing the members of a specific faction
 */
public class FactionMemberManager {

    private static final String PLAYER_NO_FACTION = "NONE";

    private ArrayList<UUID> members;
    private ArrayList<UUID> banned;

    private Faction faction;

    /**
     * Don't use this. It is for jackson (json).
     */
    public FactionMemberManager() {}

    public FactionMemberManager(Faction faction) {
        this.members = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.faction = faction;
    }

    /**
     * This event should get called by the @see {@link PlayerJoinListener#Join(PlayerJoinEvent)}
     * and shouldn't get called outside of it
     * @param event The event from PlayerJoin
     */
    public static void PlayerJoin(PlayerJoinEvent event) {
        AsyncCore.Run(() -> {
            Player player = event.getPlayer();
            Faction faction = FactionUtility.getPlayerFaction(player);

            if (faction == null) return new Result(false);
            if (faction.getFactionMemberManager().members.contains(player.getUniqueId())) return new Result(false);

            PersistentDataUtility.Write(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                    PersistentDataType.STRING, PLAYER_NO_FACTION,
                    player.getPersistentDataContainer());
            Language.sendMessage(LangMessage.FACTION_KICKED, player);
            return new Result(true);
        });
    }

    /**
     * Convert the faction members to a saveable string
     * Convert them back using @see {@link #readFromFile(Faction, String)}
     * @return The string that can be saved to a file to load it back when needed
     */
    public String saveToString() {
        String result = "";
        for (int i = 0; i < members.size(); i++) {
            result += members.get(i)+((i+1) == members.size() ? "" : ",");
        }
        result += ";";

        for (int i = 0; i < banned.size(); i++) {
            result += banned.get(i)+((i+1) == banned.size() ? "" : ",");
        }

        return result;
    }

    /**
     * Converts a string built using the @see {@link #saveToString()} back into a
     * Usable class
     * @param faction The faction the Manager should be added to
     * @param factionManagerString The string from the @see {@link #saveToString()}
     * @return
     */
    public static FactionMemberManager readFromFile(Faction faction, String factionManagerString) {
        FactionMemberManager member = new FactionMemberManager(faction);

        String rawMembers = factionManagerString.split(";")[0];
        String rawBanned = factionManagerString.split(";")[1];

        for (String uuidMember : rawMembers.split(",")) {
            member.members.add(UUID.fromString(uuidMember));
        }

        for (String uuidBanned : rawBanned.split(",")) {
            member.banned.add(UUID.fromString(uuidBanned));
        }

        return member;
    }

    /**
     * Let a (online) player join a faction if not in any other
     * If the result is not a success, you can get the error message by .getMessage() @see {@link Result#getMachineMessage()} ()}
     *
     * Potential Errors:
     * PLAYER_OFFLINE, PLAYER_IN_FACTION, PLAYER_BANNED
     *
     * @param player The player that should join the faction
     */
    public Result join(Player player) {
        if (!player.isOnline()) return new Result(false).setMessages("PLAYER_OFFLINE", "It looks like "+player.getName()+" are offline");

        if (PersistentDataUtility
                .Read(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                        PersistentDataType.STRING,
                        player.getPersistentDataContainer()) != PLAYER_NO_FACTION)
            return new Result(false).setMessages("PLAYER_IN_FACTION", player.getName() + " is already in a faction");

        if (banned.contains(player.getUniqueId())) return new Result(false).setMessages("PLAYER_BANNED", "Looks like " + player.getName() + " is banned from this faction");

        PersistentDataUtility.Write(
                PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING, faction.getRegistryName(),
                player.getPersistentDataContainer());

        AsyncCore.Run(() -> {
            members.add(player.getUniqueId());
            faction.getPowerManager()
                    .IncreaseMax(MainIF.getConfigManager().getValue("power.maxPowerPerPlayer"));
            return new Result(true);
        });

        return new Result(true);
    }

    /**
     * Let a (online) player leave the faction.
     * If the player could / is offline, please use @see {@link #kick(UUID)}
     *
     * If the result is not a success, you can get the error message by .getMessage() @see {@link Result#getMachineMessage()} ()}
     * Potential Errors: PLAYER_OFFLINE, PLAYER_NOT_IN_FACTION
     *
     * @param player The player that should leave
     */
    public Result leave(Player player) {
        if (!player.isOnline()) return new Result(false).setMessages("PLAYER_OFFLINE", "It looks like "+player.getName()+" are offline");

        if (PersistentDataUtility
                .Read(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING,
                player.getPersistentDataContainer()) == PLAYER_NO_FACTION)
            return new Result(false).setMessages("PLAYER_NOT_IN_FACTION", player.getName() + " is in no faction. So nothing can be left");

        PersistentDataUtility.Write(
                PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING, PLAYER_NO_FACTION,
                player.getPersistentDataContainer());

        AsyncCore.Run(() -> {
            members.remove(player.getUniqueId());
            faction.getPowerManager()
                    .IncreaseMax(MainIF.getConfigManager().getValue("power.maxPowerPerPlayer"));
            return new Result(true);
        });

        return new Result(true);
    }

    /**
     * Kick a player froma faction.
     * When you kick someone, he/she doesn't need to be online
     * @param player The uuid of the player you want to kick
     */
    public Result kick(UUID player) {
        members.remove(player);
        return new Result(true);
    }

    /**
     * This will kick the player & won't let him join this faction again
     * You can ban everyone. The player doesn't need to be online
     * @param player The uuid of the player you want to ban permanently
     */
    public Result ban(UUID player) {
        banned.add(player);
        kick(player);
        return new Result(true);
    }

    /**
     * This "forgives" a player who has been banned using the function ban
     * @see #ban(UUID)
     * @param player
     */
    public Result pardon(UUID player) {
        banned.remove(player);
        return new Result(true);
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public ArrayList<UUID> getBanned() {
        return banned;
    }

    public FactionMemberManager setMembers(ArrayList<UUID> members) {
        this.members = members;
        return this;
    }

    public FactionMemberManager setBanned(ArrayList<UUID> banned) {
        this.banned = banned;
        return this;
    }

    @JsonIgnore
    public FactionMemberManager setFaction(Faction faction) {
        this.faction = faction;
        return this;
    }

    @JsonIgnore
    public Faction getFaction() {
        return faction;
    }
}
