package io.github.toberocat.core.utility.factions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.sun.tools.javac.Main;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.config.Config;
import io.github.toberocat.core.utility.data.DataAccess;
import io.github.toberocat.core.utility.data.PersistentDataUtility;
import io.github.toberocat.core.utility.events.ConfigSaveEvent;
import io.github.toberocat.core.utility.factions.members.FactionMemberManager;
import io.github.toberocat.core.utility.factions.power.PowerManager;
import io.github.toberocat.core.utility.factions.rank.OwnerRank;
import io.github.toberocat.core.utility.factions.rank.Rank;
import io.github.toberocat.core.utility.json.JsonUtility;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.language.Parseable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Faction {

    private static final Map<String, Faction> LOADED_FACTIONS = new HashMap<>();

    public enum OpenType { PUBLIC, PRIVATE }

    private PowerManager powerManager;
    private FactionMemberManager factionMemberManager;

    private OpenType openType;
    private String displayName, registryName;

    public static Result<Faction> CreateFaction(String displayName, Player owner) {
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
        Faction newFaction = new Faction(displayName, registryName, OpenType.PUBLIC);
        newFaction.Join(owner, Rank.fromString(OwnerRank.registry));

        LOADED_FACTIONS.put(registryName, newFaction);
        return new Result<Faction>(true).setPaired(newFaction);
    }

    /**
     * Don't use this. iT's for jackson (json).
     */
    public Faction() {

    }

    private Faction(String displayName, String registryName, OpenType openType) {
        this.openType = openType;
        this.registryName = registryName;
        this.displayName = displayName;
        this.powerManager = new PowerManager(this,
               MainIF.getConfigManager().getValue("power.maxDefaultFaction"));
        this.factionMemberManager = new FactionMemberManager(this);
    }

    public void Join(Player player, Rank rank) {

    }

    public boolean Leave(UUID member) {
        OfflinePlayer ofPlayer = Bukkit.getOfflinePlayer(member);
        if (ofPlayer.isOnline()) {
            Player player = ofPlayer.getPlayer();
            PersistentDataUtility.Write(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                    PersistentDataType.STRING, null,
                    player.getPersistentDataContainer());
            Language.sendMessage(LangMessage.FACTION_LEAVE_SUCCESS, player,
                    new Parseable("faction_display", displayName));
        } else {
            //ToDo: Leave offline player
        }
        return true;
    }


    /**
     * Get if the plugin finds a file with the registry name
     * Use to check if a faction exists, but isn't loaded
     * @param registry The registry name the faction should be known of
     * @return True if a file with the registryName exists in ImprovedFactions/Data/Factions
     */
    public static boolean isFactionRegistryInStorage(String registry) {
        return Arrays.asList(DataAccess.listFiles("Factions")).contains(registry);
    }


    /**
     * Get if the plugin finds a file with the registry name
     * Use to check if a faction exists, but isn't loaded
     * @param display The registry name the faction should be known of
     * @return True if a file with the registryName exists in ImprovedFactions/Data/Factions
     */
    public static boolean isFactionDisplayInStorage(String display) {
        String registry = factionDisplayToRegistry(display);
        return isFactionRegistryInStorage(registry);
    }

    public static boolean Delete(Faction faction) {
        for (UUID member : getAllMembers(faction)) {
            faction.Leave(member);
        }
        LOADED_FACTIONS.remove(faction.getRegistryName());
        return true;
    }

    public static boolean hasFactionByDisplayName(String displayName) {
        String registryName = factionDisplayToRegistry(displayName);
        return LOADED_FACTIONS.containsKey(registryName);
    }

    public static boolean hasFactionByRegistry(String registryName) {
        return LOADED_FACTIONS.containsKey(registryName);
    }

    public static Faction getFactionByRegistry(String registryName) {
        return LOADED_FACTIONS.get(registryName);
    }

    public static Faction getFactionByDisplayName(String displayName) {
        String registryName = factionDisplayToRegistry(displayName);
        return LOADED_FACTIONS.get(registryName);
    }

    public static List<UUID> getAllMembers(Faction faction) {
        String loadedFaction = DataAccess.getRawFile("Factions", faction.getRegistryName(),
                Faction.class);

        //ToDo: Get faction members from rawData
        return null;
    }

    public static void unload() {
        LOADED_FACTIONS.clear();
    }

    public static String factionDisplayToRegistry(String displayName) {
        return  ChatColor.stripColor(displayName.replaceAll("[^a-zA-Z0-9]", " "));
    }

    public static boolean EnableFactions() {

        for (String file : DataAccess.listFiles("Factions")) {
            Faction faction = DataAccess.getFile("Factions", file.split("\\.")[0], Faction.class);
            if (faction == null) return false;
            faction.getFactionMemberManager().setFaction(faction);
            faction.getPowerManager().setFaction(faction);
        }

        MainIF.getIF().getSaveEvents().add(new ConfigSaveEvent() {
            @Override
            public SaveType isSingleCall() {
                return SaveType.DataAccess;
            }

            @Override
            public Result SaveDataAccess() {
                for (Faction faction : LOADED_FACTIONS.values()) {
                    DataAccess.AddFile("Factions", faction.getRegistryName(), faction);
                }

                return new Result<>(true).setMachineMessage("Factions/*.json");
            }
        });
        return true;
    }

    public static List<String> getAllFactions() {
        LinkedList<String> list = new LinkedList<>();
        for (String str : DataAccess.listFiles("Factions")) {
            list.add(str.split("\\.")[0]);
        }
        return list;
    }

    public static Map<String, Faction> getLoadedFactions() {
        return LOADED_FACTIONS;
    }

    //<editor-fold desc="Getters and Setters">
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

    //</editor-fold>
}
