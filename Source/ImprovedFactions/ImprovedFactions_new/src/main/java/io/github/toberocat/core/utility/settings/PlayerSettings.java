package io.github.toberocat.core.utility.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.data.DataAccess;
import io.github.toberocat.core.utility.events.ConfigSaveEvent;
import io.github.toberocat.core.utility.json.JsonUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerSettings {

    @JsonIgnore
    private static final Map<UUID, PlayerSettings> SETTINGS = new HashMap();
    @JsonIgnore
    public static final Map<String, Setting> DEFAULT_SETTINGS = new HashMap<>();
    @JsonIgnore
    private static final ArrayList<UUID> PREPARING = new ArrayList<>();

    private UUID playerUUID;
    private String name;
    private Map<String, Setting> playerSettings;

    public PlayerSettings() {}

    private PlayerSettings(UUID playerUUID, String name) {
        this.playerUUID = playerUUID;
        this.name = name;
        this.playerSettings = DEFAULT_SETTINGS;
    }

    private PlayerSettings(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.name = Bukkit.getOfflinePlayer(playerUUID).getName();
        this.playerSettings = DEFAULT_SETTINGS;
    }

    private PlayerSettings(String name) {
        this.playerUUID = Bukkit.getOfflinePlayer(name).getUniqueId();
        this.name = name;
        this.playerSettings = DEFAULT_SETTINGS;
    }

    public static Result<PlayerSettings> getSettings(UUID uuid) {
        if (PREPARING.contains(uuid)) {
            return new Result<PlayerSettings>(false).setMessages("CURRENTLY_BUSY_PREPERING",
                    "&cYou can't access this currently. Your data is being prepared. Please try later");
        }

        return new Result<PlayerSettings>(true).setPaired(SETTINGS.get(uuid));
    }
    public static void PlayerJoined(final UUID joinedPlayer) {
        PREPARING.add(joinedPlayer);
        AsyncCore.Run(() -> {
            PlayerSettings settings = null;
            if (DataAccess.exists("Players", joinedPlayer.toString())) {
                settings = DataAccess.getFile("Players", joinedPlayer.toString(), PlayerSettings.class);

                for (String key : DEFAULT_SETTINGS.keySet()) {
                    Setting defaultSettings = DEFAULT_SETTINGS.get(key);

                    settings.playerSettings.get(key).setType(defaultSettings.getType());
                    settings.playerSettings.get(key).setMaterial(defaultSettings.getMaterial());
                }
            } else {
                settings = new PlayerSettings(joinedPlayer);
            }
            SETTINGS.put(joinedPlayer, settings);
            PREPARING.remove(joinedPlayer);
        });
    }
    public static void PlayerLeave(UUID leftPlayer) {
        AsyncCore.Run(() -> {
           DataAccess.AddFile("Players", leftPlayer.toString(), SETTINGS.get(leftPlayer));
           SETTINGS.remove(leftPlayer);
        });
    }

    public static boolean init() {
        DEFAULT_SETTINGS.put("bossBars", new Setting<>(true, Setting.SettingsType.BOOL)
                .setMaterial(Material.HEART_OF_THE_SEA));
        DEFAULT_SETTINGS.put("hideCommandDescription", new Setting<>(false, Setting.SettingsType.BOOL)
                .setMaterial(Material.COMMAND_BLOCK));
        DEFAULT_SETTINGS.put("displayTitle", new Setting<>(true, Setting.SettingsType.BOOL)
                .setMaterial(Material.NAME_TAG));
        DEFAULT_SETTINGS.put("titlePosition", new Setting<>(true, Setting.SettingsType.BOOL)
                .setMaterial(Material.HEART_OF_THE_SEA));

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerJoined(player.getUniqueId());
        }

        MainIF.getIF().getSaveEvents().add(new ConfigSaveEvent() {
            @Override
            public SaveType isSingleCall() {
                return SaveType.DataAccess;
            }

            @Override
            public Result SaveDataAccess() {
                for (UUID player : SETTINGS.keySet()) {
                    if (!DataAccess.AddFile("Players", player.toString(), SETTINGS.get(player)))
                        return new Result<String>(false)
                                .setPaired(JsonUtility.SaveObject(player.toString()))
                                .setMachineMessage("Players/" + player + ".json");
                }
                SETTINGS.clear();
                return new Result<>(true).setMachineMessage("Players/*.json");
            }
        });
        return true;
    }

    @JsonIgnore
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @JsonIgnore
    public PlayerSettings setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public Map<String, Setting> getPlayerSetting() {
        return playerSettings;
    }

    public PlayerSettings setPlayerSetting(Map<String, Setting> playerSetting) {
        this.playerSettings = playerSetting;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerSettings setName(String name) {
        this.name = name;
        return this;
    }

    public static Map<UUID, PlayerSettings> getLoadedSettings() {
        return SETTINGS;
    }
}
