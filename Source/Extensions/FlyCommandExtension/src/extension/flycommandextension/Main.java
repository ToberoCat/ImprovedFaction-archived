package extension.flycommandextension;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import extension.flycommandextension.commands.FlySubCommand;
import extension.flycommandextension.listeners.ChunkChangeListener;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionRegistry;
import io.github.toberocat.improvedfactions.factions.FactionRankPermission;
import io.github.toberocat.improvedfactions.factions.FactionSettings;
import io.github.toberocat.improvedfactions.language.LangDefaultDataAddon;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.Utils;

public class Main extends Extension {

	public static final String FLY_COMMAND_DESCRIPTION = "commands.fly.description";
	public static final String FLY_COMMAND_ENABLED = "commands.fly.enabled";
	public static final String FLY_COMMAND_DISABLED = "commands.fly.disabled";
	public static final String FLY_COMMAND_DEWARNING = "commands.fly.unwarning";
	public static final String FLY_COMMAND_WARNING = "commands.fly.warning";
	public static final String FLY_COMMAND_NOT_IN_CHUNK = "commands.fly.no-in-chunk";
	public static final String FLY_COMMAND_ALLOW_FLIGHT_DISABLED = "commands.fly.allow-flight-disabled";

	public static final String ALLOW_FLIGHT = "allow_flight";
	
	@Override
	protected ExtensionRegistry register() {
		return new ExtensionRegistry("FlyCommandExtension", "1.0", new String[] {"BETAv4.0.0"});
	}
	
	@Override
	protected void OnEnable(ImprovedFactionsMain plugin) {
		super.OnEnable(plugin);
		LangMessage.langDefaultDataAddons.add(new LangDefaultDataAddon() {
			
			@Override
			public Map<String, String> Add() {
				Map<String, String> addons = new HashMap<>();
				addons.put(FLY_COMMAND_DESCRIPTION, "&fToggle flight mode");
				addons.put(FLY_COMMAND_ENABLED, "&a&lEnabled&f flight mode");
				addons.put(FLY_COMMAND_DISABLED, "&c&lDisabled&f flight mode");
				addons.put(FLY_COMMAND_DEWARNING, "&a&lWarning:&f flight mode enabled because you entered a claimed chunk");
				addons.put(FLY_COMMAND_WARNING, "&6&lWarning:&f flight mode disabled because you left the claimed chunk. You have 3 seconds left until your flying abliliy gets removed");
				addons.put(FLY_COMMAND_NOT_IN_CHUNK, "&c&lError&f Cannot enable flight mode because you are not in your faction chunk");
				addons.put(FLY_COMMAND_ALLOW_FLIGHT_DISABLED, "&c&lError&f cannot enable flight mode because allow flight is not enabled in the server.properties");

				return addons;
			}
		});
		
		FactionSettings.RANKS.put(ALLOW_FLIGHT, new FactionRankPermission(Utils.createItem(Material.ELYTRA, Language.format("&aAllow flying"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		
		FactionCommand.AddSubCommand(new FlySubCommand());
		
		plugin.getExtConfigData().getConfig().addDefault("flycommand.chunkLeaveFlightRemoval", 3);
		
		plugin.getExtConfigData().getConfig().options().copyDefaults(true);
		plugin.getExtConfigData().saveConfig();
		
		plugin.getServer().getPluginManager().registerEvents(new ChunkChangeListener(), plugin);
	}

}
