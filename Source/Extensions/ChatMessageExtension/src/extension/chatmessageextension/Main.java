package extension.chatmessageextension;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import extension.chatmessageextension.commands.ChatSubCommand;
import extension.chatmessageextension.listeners.OnChatListener;
import extension.chatmessageextension.messages.ChatMessage;
import extension.chatmessageextension.messages.History;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionRegistry;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionRankPermission;
import io.github.toberocat.improvedfactions.factions.FactionSettings;
import io.github.toberocat.improvedfactions.language.LangDefaultDataAddon;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.Debugger;
import io.github.toberocat.improvedfactions.utility.Utils;
import io.github.toberocat.improvedfactions.utility.configs.JsonObjectLoader;
import io.github.toberocat.improvedfactions.utility.configs.JsonObjectSaver;

public class Main extends Extension {

	public static final String FACTION_CHAT_SEND = "faction_chat_send";
	public static final String FACTION_CHAT_READ = "faction_chat_read";
	
	public static final String MUTED_CHAT_ENABLED = "chat_muted_enabled";
	public static final String MUTED_CHAT_DISABLED = "chat_muted_disabled";
	
	private static Map<String, History> chatHistories = new HashMap<>();
	
	private File historyFolder;
	
	@Override
	protected ExtensionRegistry register() {
		return new ExtensionRegistry("ChatMessageExtension", "2.0", new String[] {
			"BETAv3.0.0", "BETAv4.0.0"
		});
	}
	
	@Override
	public void OnEnable(ImprovedFactionsMain plugin) {
		plugin.getServer().getPluginManager().registerEvents(new OnChatListener(), plugin);
		
		plugin.getExtConfigData().getConfig().addDefault("chat.saveChatHistory", true);
		plugin.getExtConfigData().getConfig().addDefault("chat.usePrettyPrinter", true);

		plugin.getExtConfigData().getConfig().options().copyDefaults(true);
		plugin.getExtConfigData().saveConfig();;
		
		FactionSettings.RANKS.put(FACTION_CHAT_SEND, new FactionRankPermission(Utils.createItem(Material.WRITABLE_BOOK, Language.format("&aSend faction chat messages"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		FactionSettings.RANKS.put(FACTION_CHAT_READ, new FactionRankPermission(Utils.createItem(Material.KNOWLEDGE_BOOK, Language.format("&aRead faction chat messages"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(AdminRank.registry),
                Rank.fromString(OwnerRank.registry),
        }));
		
		FactionCommand.subCommands.add(new ChatSubCommand());
		
		LangMessage.langDefaultDataAddons.add(new LangDefaultDataAddon() {
			
			@Override
			public Map<String, String> Add() {
				Map<String, String> addons = new HashMap<>();
				addons.put(MUTED_CHAT_ENABLED, "&a&lMuted&f faction chat");
				addons.put(MUTED_CHAT_DISABLED, "&c&lUnmuted&f faction chat");

				return addons;
			}
		});
		
		//Chat history
		
		if (plugin.getExtConfigData().getConfig().getBoolean("chat.saveChatHistory")) {
			historyFolder = new File(plugin.getDataFolder() + "/Data/History/Chats");
			historyFolder.mkdirs();
			
			LoadChatHistory();
		}
	}
	
	@Override
	protected void OnDisable(ImprovedFactionsMain plugin) {
		super.OnDisable(plugin);
		if (plugin.getExtConfigData().getConfig().getBoolean("chat.saveChatHistory") && historyFolder != null) {
			SaveChatHistory();
		}
	}

	public void LoadChatHistory() {
		for (File historyFile : historyFolder.listFiles()) {
			if (historyFile.getName().endsWith(".log")) {
				try {
					History history = (History) JsonObjectLoader.LoadFromJson(historyFile, History.class);
					chatHistories.put(historyFile.getName().replace(".log",""), history);
				} catch (IOException e) {
					Debugger.LogSevere("§cCouldn't load " + historyFile.getName() + ". " + e.getMessage());
				}
			}
		}
	}
	
	public void SaveChatHistory() {
		for (String factionRegistry : chatHistories.keySet()) {
			History history = chatHistories.get(factionRegistry);
			
			File historyFile = new File(historyFolder.getPath() + "/" + factionRegistry + ".log");
			try {
				historyFile.createNewFile();
				JsonObjectSaver.SaveToJson(historyFile, history, ImprovedFactionsMain.getPlugin().getExtConfigData().getConfig().getBoolean("chat.usePrettyPrinter"));
			} catch (IOException e) {
				Debugger.LogSevere("§cCouldn't save " + factionRegistry + ".log. " + e.getMessage());
			}
		}
	}
	
	public static void AddChatMessage(ChatMessage message, Faction faction) {
		if (chatHistories.containsKey(faction.getRegistryName())) {
			chatHistories.get(faction.getRegistryName()).getMessages().add(message);
		} else {
			History history = new History();
			history.getMessages().add(message);
			
			chatHistories.put(faction.getRegistryName(), history);
		}
	}
}
