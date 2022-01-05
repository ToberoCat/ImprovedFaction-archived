package extension.settingsandflags;

import org.bukkit.Material;

import extension.settingsandflags.listeneea.EndermanGriefListener;
import extension.settingsandflags.listeneea.ExplosionListener;
import extension.settingsandflags.listeneea.FireSpreadListener;
import extension.settingsandflags.listeneea.InteractListnener;
import extension.settingsandflags.listeneea.MobSpawningListener;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionRegistry;
import io.github.toberocat.improvedfactions.factions.FactionRankPermission;
import io.github.toberocat.improvedfactions.factions.FactionSettings;
import io.github.toberocat.improvedfactions.gui.Flag;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.utility.Utils;

public class Main extends Extension {

	public static final String OPEN_INVENTORY_PERMISSION = "openInv";
	public static final String OPEN_DOORS_PERMISSION = "openDoors";
	public static final String USE_REDSTONE_PERMISSION = "useRedstone";
	
	public static final String FIRESPREAD = "firespread";
	public static final String EXPLOSIONS = "explosions";
	public static final String HOSTILE_MOB_SPAWNING = "hostileMobSpawning";
	public static final String ENDERMAN_PICKUP = "endermanPickup";

	@Override
	protected ExtensionRegistry register() {
		return new ExtensionRegistry("SettingsAndFlags", "2.0", new String[] {
				"BETAv3.0.0",
				"BETAv4.0.0"
		});
	}

	@Override
	protected void OnEnable(ImprovedFactionsMain plugin) {
		FactionSettings.FLAGS.put(FIRESPREAD, new Flag<>(Flag.FlagType.Boolean, Material.FLINT_AND_STEEL, "&aFirespread", true));
		FactionSettings.FLAGS.put(EXPLOSIONS, new Flag<>(Flag.FlagType.Boolean, Material.TNT, "&aExplosions", true));
		FactionSettings.FLAGS.put(HOSTILE_MOB_SPAWNING, new Flag<>(Flag.FlagType.Boolean, Material.ZOMBIE_SPAWN_EGG, "&aHostile mob spawning", true));
		FactionSettings.FLAGS.put(ENDERMAN_PICKUP, new Flag<>(Flag.FlagType.Boolean, Material.ENDER_EYE, "&aEnderman pickup", true));
		
		FactionSettings.RANKS.put(OPEN_INVENTORY_PERMISSION, new FactionRankPermission(Utils.createItem(Material.CHEST, Language.format("&aOpen inventories"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		FactionSettings.RANKS.put(OPEN_DOORS_PERMISSION, new FactionRankPermission(Utils.createItem(Material.OAK_DOOR, Language.format("&aOpen doors"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		FactionSettings.RANKS.put(USE_REDSTONE_PERMISSION, new FactionRankPermission(Utils.createItem(Material.STONE_PRESSURE_PLATE, Language.format("&aUse redstone"), null), new Rank[]{
                Rank.fromString(MemberRank.registry),
                Rank.fromString(OwnerRank.registry),
                Rank.fromString(AdminRank.registry)
        }));
		
		plugin.getServer().getPluginManager().registerEvents(new ExplosionListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new FireSpreadListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new EndermanGriefListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new InteractListnener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new MobSpawningListener(), plugin);

	}
}
