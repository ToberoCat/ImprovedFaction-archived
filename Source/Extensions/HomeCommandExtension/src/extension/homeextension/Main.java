package extension.homeextension;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import extension.homeextension.subCommands.HomeSubCommand;
import extension.homeextension.subCommands.SetHomeSubCommand;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionRegistry;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionData;
import io.github.toberocat.improvedfactions.factions.FactionRankPermission;
import io.github.toberocat.improvedfactions.factions.FactionSettings;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.Utils;
import io.github.toberocat.improvedfactions.utility.configs.DataManager;

public class Main extends Extension {

	public final static String SETHOME_PERMISSION = "sethome";
	public final static String HOME_PERMISSION = "home";
	
	public static Map<String, Location> homes = new HashMap<>();

	public static void main(String[] args) {
		System.out.println("Ohh. you got me... that's the magic behind the extension system");
	}

	@Override
	protected ExtensionRegistry register() {
		return new ExtensionRegistry("HomeExtension", "3.0", new String[] {
				"BETAv3.0.0",
				"BETAv4.0.0"
		});
	}
	
	@Override
	protected void OnEnable(ImprovedFactionsMain plugin) {
		FactionSettings.RANKS.put(SETHOME_PERMISSION, new FactionRankPermission(Utils.createItem(Material.COMPASS, Language.format("&aSet home"), null), new Rank[]{
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		FactionSettings.RANKS.put(HOME_PERMISSION, new FactionRankPermission(Utils.createItem(Material.RED_BED, Language.format("&aTeleport home"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
				Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		Faction.data.add(new FactionData() {
			
			@Override
			public void Save(Faction faction, DataManager dataManager) {
				Location location = homes.get(faction.getRegistryName());
				dataManager.getConfig().set("f." + faction.getRegistryName() + ".home", location);
				dataManager.saveConfig();
			}
			
			@Override
			public void Load(Faction faction, DataManager dataManager) {
				Location location = dataManager.getConfig().getLocation("f." + faction.getRegistryName() + ".home");
				if (location != null) homes.put(faction.getRegistryName(), location);
			}
		});
		
		FactionCommand.subCommands.add(new HomeSubCommand());
		FactionCommand.subCommands.add(new SetHomeSubCommand());
	}

}
