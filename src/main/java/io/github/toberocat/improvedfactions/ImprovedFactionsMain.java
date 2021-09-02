package io.github.toberocat.improvedfactions;

import com.comphenix.protocol.ProtocolManager;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionLoader;
import io.github.toberocat.improvedfactions.extentions.list.ExtensionListLoader;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.gui.Flag;
import io.github.toberocat.improvedfactions.gui.GuiListener;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.listeners.*;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.tab.FactionCommandTab;
import io.github.toberocat.improvedfactions.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class ImprovedFactionsMain extends JavaPlugin {

    public static Map<String, Extension> extensions = new HashMap<>();

    public static Map<UUID, PlayerData> playerData = new HashMap<>();

    public static String VERSION = "BETAv2.0.1";

    public static UpdateChecker updateChecker;

    private static ImprovedFactionsMain INSTANCE;

    private PlayerMessages playerMessages;

    private GuiListener guiListener;

    private SignMenuFactory signMenuFactory;
    private ProtocolManager protocolManager;

    private DataManager languageData;
    private DataManager factionData;
    private DataManager messagesData;
    private DataManager extConfigData;

    @Override
    public void onEnable() {
        guiListener = new GuiListener();

        Rank.Init();

        System.out.println(Flag.fromString(Faction.OPENTYPE_FLAG+"::"+Faction.FLAGS[0].toString()).toString());

        //Data Managers / Config
        //Language.yml
        languageData = new DataManager(this, "language.yml");
        factionData = new DataManager(this, "factions.yml");
        messagesData = new DataManager(this, "messages.yml");
        extConfigData = new DataManager(this, "extConfig.yml");

        playerMessages = new PlayerMessages(this);
        //Config defaults
        getConfig().addDefault("factions.maxMembers", 50);
        getConfig().addDefault("factions.startClaimPower", 9);
        getConfig().addDefault("factions.maxClaimPower", 100);
        getConfig().addDefault("factions.powerPerPlayer", 3);
        getConfig().addDefault("general.updateChecker", true);
        getConfig().addDefault("general.mapViewDistance", 7);

        getConfig().options().copyDefaults(true);
        saveConfig();

        //Language defaults
        languageData.getConfig().addDefault("prefix", "&c&lFactions:");

        languageData.getConfig().options().copyDefaults(true);
        languageData.saveConfig();

        //Add commands
        getServer().getPluginCommand("f").setExecutor(new FactionCommand());
        getServer().getPluginCommand("f").setTabCompleter(new FactionCommandTab());

        //Add listeners
        getServer().getPluginManager().registerEvents(new OnWorldSaveListener(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);
        getServer().getPluginManager().registerEvents(new OnChunkEntered(), this);

        getServer().getPluginManager().registerEvents(new OnBlockBreak(), this);
        getServer().getPluginManager().registerEvents(new OnBlockPlace(), this);
        getServer().getPluginManager().registerEvents(new OnInteract(), this);
        getServer().getPluginManager().registerEvents(new OnEntityInteract(), this);
        getServer().getPluginManager().registerEvents(new OnEntityDamage(), this);
        getServer().getPluginManager().registerEvents(new OnEndCystalDestroy(), this);

        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new OnLeave(), this);

        getServer().getPluginManager().registerEvents(guiListener, this);

        ClickActions.init(this);

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            System.out.println("Can't load imrpoved factions. Need to install protocolLib");
            Bukkit.getPluginManager().disablePlugin(INSTANCE);
        }

        signMenuFactory = new SignMenuFactory(this);

        //Load extentions
        try {
            ExtensionListLoader.RegenerateExtensionList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Create extension folder
        File file = new File(getDataFolder().getPath() + "/Extensions");
        file.mkdir();

        if (file.exists()) {
            try {
                ExtensionLoader<Extension> loader = new ExtensionLoader<Extension>();
                int loaded = 0;
                for (File jar : file.listFiles()) {
                    if (jar.getName().endsWith(".jar")) {
                        Extension extension = loader.LoadClass(jar, "extension.Main", Extension.class);
                        extension.Enable(this);
                        extensions.put(extension.getRegistry().getName(), extension);
                        loaded++;

                    }
                }
                if (loaded == 0) {
                    getServer().getConsoleSender().sendMessage("§7[Factions] §cDidn't find any extensions to be loaded");
                } else {
                    getServer().getConsoleSender().sendMessage("§7[Factions] §aSuccessfully loaded " + loaded +
                            (loaded==1?" extension" : " extensions"));
                }
            } catch (ClassNotFoundException e) {
                getServer().getConsoleSender().sendMessage("§7[Factions] §cDidn't find any extensions to be loaded");
            }
        }


        //Set instance
        INSTANCE = this;

        //Others
        Faction.LoadFactions(this);

        try {
            if (getConfig().getBoolean("general.updateChecker")) {
                updateChecker = new UpdateChecker(VERSION, new URL("https://raw.githubusercontent.com/ToberoCat/ImprovedFaction/master/version.json"));
                 if(!updateChecker.isNewestVersion()) {
                    getConsoleSender().sendMessage(Language.getPrefix() +
                            "§fA newer version of this plugin is available. Check it out: https://www.spigotmc.org/resources/improved-factions.95617/");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Player on : Bukkit.getOnlinePlayers()) {
            AddPlayerData(on);
        }
    }

    @Override
    public void onDisable() {
        Faction.SaveFactions(this);
        playerMessages.SavePlayerMessages();
    }

    public DataManager getFactionData() {
        if (factionData == null) {
            Bukkit.getLogger().log(Level.WARNING, "Instance of factionData is null");
        }
        return factionData;
    }

    public DataManager getLanguageData() {
        if (languageData == null) {
            Bukkit.getLogger().log(Level.WARNING, "Instance of languageData is null");
        }
        return languageData;
    }

    public DataManager getMessagesData() {
        if (messagesData == null) {
            Bukkit.getLogger().log(Level.WARNING, "Instance of messageData is null");
        }
        return messagesData;
    }

    public DataManager getExtConfigData() {
        if (extConfigData == null) {
            Bukkit.getLogger().log(Level.WARNING, "Instance of extConfigDaat is null");
        }
        return extConfigData;
    }

    public static ImprovedFactionsMain getPlugin() {
        if (INSTANCE == null) {
            Bukkit.getLogger().log(Level.WARNING, "Instance of ImprovedFactionMain is null. Please wait until onEnable completed before calling");
        }
        return INSTANCE;
    }


    public static void AddPlayerData(Player player) {
        PersistentDataContainer container = player.getLocation().getChunk().getPersistentDataContainer();

        PlayerData data = new PlayerData();

        data.playerFaction = GetFaction(player);

        data.chunkData.isInClaimedChunk = container.has(ChunkUtils.FACTION_CLAIMED_KEY, PersistentDataType.STRING);
        if (data.chunkData.isInClaimedChunk)
            data.chunkData.factionRegistry = container.get(ChunkUtils.FACTION_CLAIMED_KEY, PersistentDataType.STRING);

        playerData.put(player.getUniqueId(), data);
    }

    private static Faction GetFaction(Player player) {
        for (Faction faction : Faction.getFactions()) {
            for (FactionMember factionMember : faction.getMembers()) {
                if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                    return faction;
                }
            }
        }
        return null;
    }

    public SignMenuFactory getSignMenuFactory() {
        return signMenuFactory;
    }

    public PlayerMessages getPlayerMessages() {
        return playerMessages;
    }

    public static ConsoleCommandSender getConsoleSender() {
        return getPlugin().getServer().getConsoleSender();
    }
}
